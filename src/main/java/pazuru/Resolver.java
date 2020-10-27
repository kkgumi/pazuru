package pazuru;

import pazuru.exception.NoResourceException;
import pazuru.exception.UnexpectedResponseException;
import pazuru.util.PazuruImg;
import pazuru.util.PazuruMenu;

import java.io.IOException;

public interface Resolver {
    boolean authenticate(String username, String password) throws IOException, UnexpectedResponseException;
    PazuruMenu list() throws IOException, UnexpectedResponseException, NoResourceException;
    PazuruImg resolve(int index) throws IOException, UnexpectedResponseException, NoResourceException;
}
