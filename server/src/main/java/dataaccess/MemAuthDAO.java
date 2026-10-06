package dataaccess;
import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class MemAuthDAO {
    private final Map<String, AuthData> users = new HashMap<>();
}
