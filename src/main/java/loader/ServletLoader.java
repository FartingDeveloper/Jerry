package loader;

import javax.servlet.Servlet;
import java.util.List;

public class ServletLoader implements Loader<Servlet>{

    public List<Servlet> load(String path) {
        this.getClass().getClassLoader();
        return null;
    }
}
