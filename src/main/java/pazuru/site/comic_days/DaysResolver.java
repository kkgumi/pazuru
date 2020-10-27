package pazuru.site.comic_days;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;
import pazuru.exception.NoResourceException;
import pazuru.exception.UnexpectedKeyException;
import pazuru.exception.UnexpectedResponseException;
import pazuru.util.*;
import pazuru.Resolver;


public class DaysResolver implements Resolver {
    private final OkHttpClient client = ClientFactory.newClient();

    String url;
    PazuruMenu menu;

    int DIVIDE_NUM = 4;
    int MULTIPLE = 8;

    DaysResolver(String url) {
        this.url = url;
    }

    @Override
    public boolean authenticate(String username, String password) throws IOException, UnexpectedResponseException {
        // TODO: check base url from url
        String base = "https://comic-days.com";
        FormBody formBody = new FormBody.Builder()
                .add("email_address", username)
                .add("password", password).build();
        Request loginRequest = new Request.Builder().url(base + "/user_account/login")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("referer", base + "/").post(formBody).build();
        try (Response loginResponse = client.newCall(loginRequest).execute()) {
            if (loginResponse.code() != 200) throw new UnexpectedResponseException();
            List<Cookie> cookies = Cookie.parseAll(loginRequest.url(), loginResponse.headers());
            client.cookieJar().saveFromResponse(loginRequest.url(), cookies);
            for (Cookie cookie : cookies) {
                if (cookie.name().equals("glsc")) return true;
            }
        }
        return false;
    }

    @Override
    public PazuruMenu list() throws IOException, UnexpectedResponseException, NoResourceException {
        try {
            String rawList;
            try {
                rawList = getRawListFromJson();
            } catch (IOException | UnexpectedResponseException e) {
                rawList = getRawListFromHtml();
            }
            JSONObject config = new JSONObject(rawList).getJSONObject("readableProduct");
            this.menu = new PazuruMenu(config.getString("title"));
            JSONArray pages = config.getJSONObject("pageStructure").getJSONArray("pages");
            for (int i = 0; i < pages.length(); i++) {
                if (pages.getJSONObject(i).has("src")) {
                    this.menu.add(String.valueOf(i+1), pages.getJSONObject(i).getString("src"));
                }
            }
            return this.menu;
        } catch (JSONException e) {
            throw new UnexpectedResponseException();
        }
    }

    @Override
    public PazuruImg resolve(int index) throws IOException, UnexpectedResponseException, NoResourceException {
        PazuruMenuItem entity = this.menu.get(index);
        Request imgRequest = new Request.Builder().url(entity.getUrl()).addHeader("Referer", this.url).build();
        try (Response imgResponse = client.newCall(imgRequest).execute()) {
            if (imgResponse.code() != 200) throw new NoResourceException();
            if (!imgResponse.header("content-type", "*/*").matches("^image/.*$"))
                throw new UnexpectedResponseException();
            PazuruImg img = new PazuruImg(imgResponse.body().byteStream());
            try {
                RegularPazuruKey key = new RegularPazuruKey(img.getHeight(), img.getWidth(), this.DIVIDE_NUM, this.DIVIDE_NUM,
                        (img.getHeight() / (this.DIVIDE_NUM * this.MULTIPLE)) * this.MULTIPLE,
                        (img.getWidth() / (this.DIVIDE_NUM * this.MULTIPLE)) * this.MULTIPLE);
                key.set(new int[]{0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15});
                img.setKey(key);
            } catch (UnexpectedKeyException e) {
                Logger.getLogger("pazuru").warning("fail to apply key");
            }
            return img;
        }
    }

    public String getRawListFromJson() throws IOException, UnexpectedResponseException {
        HttpUrl url = HttpUrl.parse(this.url).newBuilder().setPathSegment(
                1, HttpUrl.parse(this.url).pathSegments().get(1) + ".json").build();
        Request pageRequest = new Request.Builder().get().url(url).build();
        try (Response pageResponse = client.newCall(pageRequest).execute()) {
            if (pageResponse.code() != 200) throw new UnexpectedResponseException();
            return pageResponse.body().string();
        }
    }

    public String getRawListFromHtml() throws IOException, UnexpectedResponseException {
        Request pageRequest = new Request.Builder().get().url(this.url).build();
        try (Response pageResponse = client.newCall(pageRequest).execute()) {
            if (pageResponse.code() != 200) throw new UnexpectedResponseException();
            Document document = Jsoup.parse(pageResponse.body().string());
            String episodeJson = document.getElementById("episode-json").attr("data-value");
            return unescapeHtml4(episodeJson);
        }
    }
}
