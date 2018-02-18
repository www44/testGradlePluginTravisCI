package simple;

import org.junit.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class ContainerTest {
    @ClassRule
    public static GenericContainer jenkinsContainer = new GenericContainer(
            new ImageFromDockerfile()
                    .withFileFromClasspath("Dockerfile", "docker/jenkins_2.60.3/Dockerfile")
                    .withFileFromClasspath("config.xml", "docker/jenkins_2.60.3/config.xml")
                    .withFileFromClasspath("plugins.txt", "docker/jenkins_2.60.3/plugins.txt"))
            .withExposedPorts(8080);
    /*public static GenericContainer jenkinsContainer = new GenericContainer("jenkins:test")
            .withExposedPorts(8080);
*/
    private static URL JENKINS_URL;

    @BeforeClass
    public static void waitForJenkinsStartUp() throws MalformedURLException, InterruptedException {
        //init jenkins URL
        JENKINS_URL = new URL("http://" + jenkinsContainer.getContainerIpAddress()
                + ":" + jenkinsContainer.getMappedPort(8080));

        //wait for jenkins service initialisation
        int tries = 128;
        int secondsToWait = 3;
        HttpURLConnection jenkinsConnection;
        System.out.println("Waiting for jenkins starting.");
        for (int c = 0; c < tries; c++) {
            try {
                jenkinsConnection = (HttpURLConnection) JENKINS_URL.openConnection();
                jenkinsConnection.setRequestMethod("GET");
                jenkinsConnection.getInputStream();
                return;
            } catch (Exception e) {
                System.out.println("Connection is not ready, wait for " + secondsToWait + " seconds.");
                Thread.sleep(1000 * secondsToWait);
            }
        }
    }

    public HttpURLConnection jenkinsConnection;

    @Before
    public void jenkinsConnectionInit() throws IOException, InterruptedException {
        jenkinsConnection = (HttpURLConnection) JENKINS_URL.openConnection();
    }

    @After
    public void jenkinsConnectionClose() {
        jenkinsConnection.disconnect();
    }

    @Test
    public void test1() throws IOException, InterruptedException {
        jenkinsConnection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(jenkinsConnection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        String prefix = "Request to " + jenkinsConnection.getURL() + ": ";
        System.out.println(prefix + jenkinsConnection.getResponseMessage());
    }

    @Test
    public void test2() throws IOException {
        jenkinsConnection = (HttpURLConnection)new URL(JENKINS_URL,"api/json").openConnection();
        jenkinsConnection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(jenkinsConnection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        String prefix = "Request to " + jenkinsConnection.getURL() + ": ";
        System.out.println(prefix + jenkinsConnection.getResponseMessage());
        System.out.println(prefix + content);
    }
}
