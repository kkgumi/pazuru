package pazuru;

import pazuru.exception.NoMatchRuleException;
import pazuru.exception.NoResourceException;
import pazuru.exception.UnexpectedResponseException;
import pazuru.site.comic_days.DaysRule;
import pazuru.site.comic_fuz.FuzRule;
import pazuru.util.FileUtil;
import pazuru.util.PazuruImg;
import pazuru.util.PazuruMenu;
import pazuru.util.PazuruMenuItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Core {
    ArrayList<Rule> rules = new ArrayList<>();
    Resolver resolver = null;
    String username;
    String password;
    Logger logger = Logger.getLogger("pazuru");

    PazuruMenu list;

    public Core(String url, String username, String password, String savingPath) throws NoMatchRuleException {
        this.rules.add(new FuzRule());
        this.rules.add(new DaysRule());

        this.username = username;
        this.password = password;
        for (Rule rule : this.rules) {
            if (rule.match(url)) {
                this.resolver = rule.getResolver(url);
                logger.config("match rule " + rule.getClass().getName());
                break;
            }
        }
        if (this.resolver == null) throw new NoMatchRuleException();
        FileUtil.setSavingPath(savingPath);
    }

    void run() throws IOException, UnexpectedResponseException, NoResourceException {
        if (!resolver.authenticate(username, password)) logger.info("fail to authenticate");
        else logger.config("authentication success");
        list = resolver.list();
        logger.info("list success; " + list.size() + " image(s)");
        for (int i = 0; i < list.size(); i++) {
            PazuruImg img = resolver.resolve(i);
            FileUtil.saveToLocal(img.getImg(), FileUtil.indexFileName(i, list.size()));
        }
        logger.info("done!");
    }

    public boolean authenticate() throws IOException, UnexpectedResponseException {
        return resolver.authenticate(username, password);
    }

    public PazuruMenu list() throws UnexpectedResponseException, NoResourceException, IOException {
        list = resolver.list();
        return resolver.list();
    }

    public void save(int from, int to, String path) throws UnexpectedResponseException, NoResourceException, IOException {
        from -= 1;
        if (to < 0) to = list.size();
        for (int i = from; i < to; i++) {
            PazuruImg img = resolver.resolve(i);
            FileUtil.saveToLocal(img.getImg(), FileUtil.indexFileName(i, list.size()));
        }
    }
}
