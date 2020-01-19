package gameClient;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;


class KML_Logger {

    private int level;
    private StringBuffer s_buf;


    KML_Logger(int level) {
        this.level = level;
        s_buf = new StringBuffer();
        KML_Start();
    }


    private void KML_Start()
    {
        s_buf.append(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
                        "<kml xmlns=\"http://earth.google.com/kml/2.2\">\r\n" +
                        "  <Document>\r\n" +
                        "    <name>" + "Game stage :"+level + "</name>" +"\r\n"+
                        " <Style id=\"node\">\r\n" +
                        "      <IconStyle>\r\n" +
                        "        <Icon>\r\n" +
                        "          <href>http://maps.google.com/mapfiles/kml/pal3/icon35.png</href>\r\n" +
                        "        </Icon>\r\n" +
                        "        <hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n" +
                        "      </IconStyle>\r\n" +
                        "    </Style>" +
                        " <Style id=\"fruit_-1\">\r\n" +
                        "      <IconStyle>\r\n" +
                        "        <Icon>\r\n" +
                        "          <href>https://ya-webdesign.com/images250_/google-map-icon-png-5.png</href>\r\n" +
                        "        </Icon>\r\n" +
                        "        <hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n" +
                        "      </IconStyle>\r\n" +
                        "    </Style>" +
                        " <Style id=\"fruit_1\">\r\n" +
                        "      <IconStyle>\r\n" +
                        "        <Icon>\r\n" +
                        "          <href>https://static.wixstatic.com/media/20c715_d3b8c7feee414b479ebfcfa9e6ee5bf7~mv2.png</href>\r\n" +
                        "        </Icon>\r\n" +
                        "        <hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n" +
                        "      </IconStyle>\r\n" +
                        "    </Style>" +
                        " <Style id=\"robot\">\r\n" +
                        "      <IconStyle>\r\n" +
                        "        <Icon>\r\n" +
                        "          <href>https://toppng.com/public/uploads/thumbnail/in-location-map-icon-navigation-symbol-ma-google-maps-marker-blue-11562916561ezoahrqazu.png</href>\r\n" +
                        "        </Icon>\r\n" +
                        "        <hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n" +
                        "      </IconStyle>\r\n" +
                        "    </Style>"
        );
    }


    void location_sampling(String id, String location)
    {
        LocalDateTime time = LocalDateTime.now();
        s_buf.append(
                "    <Placemark>\r\n" +
                        "      <TimeStamp>\r\n" +
                        "        <when>" + time+ "</when>\r\n" +
                        "      </TimeStamp>\r\n" +
                        "      <styleUrl>#" + id + "</styleUrl>\r\n" +
                        "      <Point>\r\n" +
                        "        <coordinates>" + location + "</coordinates>\r\n" +
                        "      </Point>\r\n" +
                        "    </Placemark>\r\n"
        );
    }

    /**
     * mark the kml the end of the script
     */
    void KML_END()
    {
        s_buf.append("  \r\n</Document>\r\n" +
                "</kml>");
        SaveFile();
    }

    /**
     * save the kml string to a file
     */
    private void SaveFile(){
        try
        {
            File file=new File("data/"+level+".kml");
            PrintWriter pw=new PrintWriter(file);
            pw.write(s_buf.toString());
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}