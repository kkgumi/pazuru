package pazuru.site.comic_fuz;

import okhttp3.HttpUrl;
import pazuru.Resolver;
import pazuru.Rule;

public class FuzRule implements Rule {
    @Override
    public boolean match(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        return httpUrl.host().equals("comic-fuz.com") &&
                httpUrl.pathSegments().size() == 1 &&
                httpUrl.pathSegments().get(0).equals("viewer.html") &&
                httpUrl.queryParameterValues("cid").size() == 1;
    }

    @Override
    public Resolver getResolver(String url) {
        return new FuzResolver(url);
    }
}
