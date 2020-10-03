package nl.bergac.tcxmerger;

import javax.xml.bind.JAXBException;
import java.io.File;

import static java.lang.String.format;

public class MainApplication {

    public static void main(String[] args) throws JAXBException {
        log(format("Selected route file: %s", args[0]));
        log(format("Selected hr file: %s", args[1]));

        var routeFile = new File(args[0]);
        var heartRateFile = new File(args[1]);

        new TcxMerger(routeFile, heartRateFile).merge();
    }

    private static void log(String message) {
        System.out.println(message);
    }
}
