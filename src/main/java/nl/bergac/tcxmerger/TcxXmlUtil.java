package nl.bergac.tcxmerger;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import java.io.File;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class TcxXmlUtil {
    private final JAXBContext jaxbContext;
    private final Unmarshaller unmarshaller;
    private final Marshaller marshaller;

    public TcxXmlUtil() {
        this.jaxbContext = requireNonNull(createJaxbContext());
        this.unmarshaller = requireNonNull(createUnmarshaller());
        this.marshaller = requireNonNull(createMarshaller());
    }

    private JAXBContext createJaxbContext() {
        try {
            return JAXBContext.newInstance(TrainingCenterDatabaseT.class);
        } catch (JAXBException ex) {
            System.err.println(ex);
            return null;
        }
    }

    public TrainingCenterDatabaseT unmarshall(File file) {
        requireNonNull(file);
        if (file.exists()) {
            return unmarshallFile(file).getValue();
        }
        throw new InvalidFileException(String.format("File does not exist. Absolute path: %s", file.getAbsolutePath()));
    }

    public void marshallFile(TrainingCenterDatabaseT object) {
        System.out.println("Marshalling object to file");
        File file = new File("test.xml");
        var jaxbElement = new JAXBElement(
                new QName("uri", "local"),
                TrainingCenterDatabaseT.class,
                object);
        try {
            marshaller.marshal(jaxbElement, file);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to marshal file: " + file.getPath(), e);
        }
    }

    private JAXBElement<TrainingCenterDatabaseT> unmarshallFile(File file) {
        System.out.println(format("Unmarshalling file '%s'", file.getAbsolutePath()));
        try {
           return (JAXBElement<TrainingCenterDatabaseT>) unmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to unmarshal file: " + file.getPath(), e);
        }
    }

    private Unmarshaller createUnmarshaller() {
        try {
            return jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Marshaller createMarshaller() {
        try {
            return jaxbContext.createMarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }
}
