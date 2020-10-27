package pazuru.site.comic_days;

import pazuru.Rule;
import okhttp3.HttpUrl;
import pazuru.Resolver;


public class DaysRule implements Rule {
    @Override
    public boolean match(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        return (httpUrl.host().equals("comic-days.com") ||
                httpUrl.host().equals("pocket.shonenmagazine.com") ||
                httpUrl.host().equals("comic-zenon.com")) &&
                httpUrl.pathSegments().size() == 2 &&
                (httpUrl.pathSegments().get(0).equals("magazine") ||
                        httpUrl.pathSegments().get(0).equals("volume") ||
                        httpUrl.pathSegments().get(0).equals("episode"));
    }

    @Override
    public Resolver getResolver(String url) {
        return new DaysResolver(url);
    }
}
