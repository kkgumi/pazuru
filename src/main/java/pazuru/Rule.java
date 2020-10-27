package pazuru;

public interface Rule {
    boolean match (String url);
    Resolver getResolver (String url);
}
