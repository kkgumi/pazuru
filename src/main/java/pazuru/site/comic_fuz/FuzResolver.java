package pazuru.site.comic_fuz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.jsoup.Jsoup;
import pazuru.exception.NoResourceException;
import pazuru.exception.UnexpectedResponseException;
import pazuru.util.*;
import pazuru.Resolver;


public class FuzResolver implements Resolver {
    private final OkHttpClient client = ClientFactory.newClient();

    String cid;
    String keyPairId, policy, signature, resourceUrl;
    PazuruMenu menu;
    List<FuzSize> sizes = new ArrayList<>();

    int BLOCK_WIDTH = 64;
    int BLOCK_HEIGHT = 64;

    FuzResolver(String url) {
        Logger.getLogger("pazuru").info("❤ The Comic-Fuz Solver is greatly inspired by EnkanRec's work.");
        this.cid = HttpUrl.parse(url).queryParameterValues("cid").get(0);
    }

    @Override
    public boolean authenticate(String username, String password) throws IOException, UnexpectedResponseException {
        Request loginPageRequest = new Request.Builder().get().url("https://comic-fuz.com/accounts/sign_in").build();
        Response loginPageResponse = client.newCall(loginPageRequest).execute();
        if (!loginPageResponse.isSuccessful()) loginPageResponse.close();
        if (loginPageResponse.code() != 200) throw new UnexpectedResponseException();
        String loginPageBody = loginPageResponse.body().string();
        String token = Jsoup.parse(loginPageBody).select("#new_account>input[name=authenticity_token]")
                .first().attr("value");
        FormBody formBody = new FormBody.Builder()
                .add("utf8", "✓")
                .add("authenticity_token", token)
                .add("account[email]", username)
                .add("account[password]", password)
                .add("account[remember_me]", "true")
                .add("commit", "ログイン").build();
        Request loginRequest = new Request.Builder().url("https://comic-fuz.com/accounts/sign_in")
                .addHeader("referer", "https://comic-fuz.com/accounts/sign_in").post(formBody).build();
        try (Response loginResponse = client.newCall(loginRequest).execute()) {
            if (loginResponse.code() != 200) throw new UnexpectedResponseException();
            List<Cookie> cookies = Cookie.parseAll(loginRequest.url(), loginResponse.headers());
            client.cookieJar().saveFromResponse(loginRequest.url(), cookies);
            for (Cookie cookie : cookies) {
                if (cookie.name().equals("login") && cookie.value().equals("true")) return true;
            }
        }
        return false;
    }

    @Override
    public PazuruMenu list() throws IOException, UnexpectedResponseException, NoResourceException {
        getLicence();
        this.menu = new PazuruMenu("fuz");
        String configUrl = HttpUrl.parse(this.resourceUrl).newBuilder().addPathSegment("configuration_pack.json")
                .addQueryParameter("Key-Pair-Id", this.keyPairId)
                .addQueryParameter("Policy", this.policy)
                .addQueryParameter("Signature", this.signature).build().toString();
        Request configRequest = new Request.Builder().get().url(configUrl).build();
        try (Response configResponse = client.newCall(configRequest).execute()) {
            if (configResponse.code() != 200) throw new UnexpectedResponseException();
            try {
                JSONObject jsonObject = new JSONObject(configResponse.body().string());
                JSONArray contents = jsonObject.getJSONObject("configuration").getJSONArray("contents");
                for (int i = 0; i < contents.length(); i++) {
                    String url = contents.getJSONObject(i).getString("file");
                    JSONObject metadata = jsonObject.getJSONObject(url).getJSONObject("FileLinkInfo")
                            .getJSONArray("PageLinkInfoList").getJSONObject(0).getJSONObject("Page").getJSONObject("ContentArea");
                    this.menu.add(String.valueOf(i+1), url);
                    sizes.add(new FuzSize(metadata.getInt("Width"), metadata.getInt("Height")));
                }
                return this.menu;
            } catch (JSONException e) {
                throw new UnexpectedResponseException();
            }
        }
    }

    @Override
    public PazuruImg resolve(int index) throws IOException, UnexpectedResponseException, NoResourceException {
        PazuruMenuItem entity = this.menu.get(index);
        String url = HttpUrl.parse(this.resourceUrl + entity.getUrl() + "/0.jpeg").newBuilder().addQueryParameter("Policy", this.policy)
                .addQueryParameter("Signature", this.signature)
                .addQueryParameter("Key-Pair-Id", this.keyPairId).build().toString();
        Request imgRequest = new Request.Builder().url(url).build();
        try (Response imgResponse = client.newCall(imgRequest).execute()) {
            if (imgResponse.code() != 200) throw new NoResourceException();
            if (!imgResponse.header("content-type", "*/*").matches("^image/.*$"))
                throw new UnexpectedResponseException();
            PazuruImg img = new PazuruImg(imgResponse.body().byteStream());

            img.setKey(PuzzleMap.getPuzzleMap(
                    img.getWidth(), img.getHeight(), BLOCK_WIDTH, BLOCK_HEIGHT,
                    PuzzleMap.calculateKey(entity.getUrl())));

            img.corp(sizes.get(index).width, sizes.get(index).height);
            return img;
        }
    }

    private void getLicence() throws IOException, NoResourceException, UnexpectedResponseException {
        Request licenceRequest = new Request.Builder().get()
                .url(HttpUrl.parse("https://comic-fuz.com/api4js/contents/license").newBuilder()
                        .addQueryParameter("cid", this.cid).build().toString())
                .build();
        try (Response licenceResponse = client.newCall(licenceRequest).execute()) {
            if (licenceResponse.code() != 200) throw new NoResourceException();
            try {
                JSONObject jsonObject = new JSONObject(licenceResponse.body().string());
                if (!jsonObject.getString("status").equals("200")) throw new NoResourceException();
                this.keyPairId = jsonObject.getJSONObject("auth_info").getString("Key-Pair-Id");
                this.policy = jsonObject.getJSONObject("auth_info").getString("Policy");
                this.signature = jsonObject.getJSONObject("auth_info").getString("Signature");
                this.resourceUrl = jsonObject.getString("url");
                Logger.getLogger("pazuru").fine("got licence");
            } catch (JSONException e) {
                throw new UnexpectedResponseException();
            }
        }
    }

    static class FuzSize {
        int width, height;

        public FuzSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}
