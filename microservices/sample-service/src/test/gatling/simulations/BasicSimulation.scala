import io.gatling.core.Predef._ 
import io.gatling.http.Predef._
import scala.concurrent.duration._
import com.scaffold.sample.app.SampleApplication
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext

class BasicSimulation extends Simulation { 

  val app: ConfigurableApplicationContext = SpringApplication.run(classOf[SampleApplication])
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = app.stop()
    })
  
  val httpConf = http 
    .baseURL("http://localhost:7475") 
    .acceptHeader("apllication/hal+json,text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") 
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val scn = scenario("BasicSimulation") 
    .exec(http("request_1")  
    .get("/api/sample")) 
    .pause(1) 

  setUp( 
    scn.inject(rampUsers(100) over(5 seconds)) 
  ).protocols(httpConf)
}