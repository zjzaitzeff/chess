package server;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.*;
import io.javalin.http.Handler;

public class ClearHandler implements Handler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearServiceClass) {
        this.clearService = clearServiceClass;
    }

    @Override
    public void handle(Context context) throws DataAccessException {
            ClearResult clearResult = clearService.clear();
            context.status(200).json(clearResult);
    }
}
