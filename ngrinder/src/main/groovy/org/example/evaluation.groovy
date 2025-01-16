import java.util.concurrent.atomic.AtomicInteger

import static net.grinder.script.Grinder.grinder
import static org.junit.Assert.*
import static org.hamcrest.Matchers.*
import net.grinder.script.GTest
import net.grinder.scriptengine.groovy.junit.GrinderRunner
import net.grinder.scriptengine.groovy.junit.annotation.BeforeProcess
import net.grinder.scriptengine.groovy.junit.annotation.BeforeThread
// import static net.grinder.util.GrinderUtils.* // You can use this if you're using nGrinder after 3.2.3
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.ngrinder.http.HTTPRequest
import org.ngrinder.http.HTTPRequestControl
import org.ngrinder.http.HTTPResponse
import org.ngrinder.http.cookie.Cookie
import groovy.json.JsonSlurper

/**
 * A simple example using the HTTP plugin that shows the retrieval of a single page via HTTP.
 *
 * This script is automatically generated by ngrinder.
 *
 * @author admin
 */
@RunWith(GrinderRunner)
class TestRunner {

    public static GTest test
    public static HTTPRequest request
    public static Map<String, String> headers = [:]
    public static Map<String, Object> params = [:]
    public static List<Cookie> cookies = []
    public static final String logicServerAddr = System.getenv('LOGIC_SERVER_ADDR');
    public static final String queueServerAddr = System.getenv('QUEUE_SERVER_ADDR');
    public static final String password = System.getenv('PASSWORD');
    public static final AtomicInteger number = new AtomicInteger(1); // 스레드 안전한 전역 변수
    private static final ThreadLocal<String> threadLoginBody = new ThreadLocal<>(); // 스레드별 loginBody 저장
    // TODO: 쿠키 파싱을 어떻게 할까요??
    private static final ThreadLocal<List<Cookie>> threadCookie = new ThreadLocal<>();


    @BeforeProcess
    public static void beforeProcess() {
        HTTPRequestControl.setConnectionTimeout(300000);
        test = new GTest(1, "로그인");
        request = new HTTPRequest();
        headers.put("Content-Type", "application/json");
        grinder.logger.info("Before process: HTTPRequest and headers initialized.");
    }

    @BeforeThread
    public void beforeThread() {
        test.record(this, "login");
        grinder.statistics.delayReports = true;

        // 동기화 없이 AtomicInteger로 전역 number 값 증가 및 이메일 생성
        String email = "test" + number.getAndIncrement() + "@gmail.com;user";
        String loginBody = """
    {
        "email": "${email}",
        "password": "${password}"
    }
    """;

        // 스레드별 loginBody 설정
        threadLoginBody.set(loginBody);

        grinder.logger.info("Thread {} assigned email: {} with loginBody: {}", grinder.threadNumber, email, loginBody);
    }

    @Before
    public void before() {
        request.setHeaders(headers);
        grinder.logger.info("Before: Headers and cookies initialized.");
    }

    @Test
    public void login() {
        // 스레드별 loginBody 가져오기
        String loginBody = threadLoginBody.get();

        HTTPResponse response = request.POST(
                "${logicServerAddr}/login",
                loginBody.getBytes()
        );

        if (response.statusCode != 200) {
            grinder.logger.warn("[*** Status NOT 200 ***] Response Code: {}, Response Body: {}", response.statusCode, response.getBodyText());
        } else {
            def responseBody = response.getBodyText();
            grinder.logger.info("Response Body: {}", responseBody);

            try {
                // TODO 헤더에서 쿠키값 추출 (쿠키 파싱 !!! 어떻게 할꺼야!!!!!)
                List<String> cookies = response.getHeaders("Set-Cookie");

                if (cookies) {
                    grinder.logger.info("Cookies received:");
                    cookies.each { cookie ->
                        String cookieString = cookie.toString();
                        if (cookieString.startsWith("Set-Cookie: ")) {
                            cookieString = cookieString.replaceFirst("Set-Cookie: ", "");
                        }
                        // TODO 쿠키 파싱!!!!!
                        if (cookieString.startsWith("AToken=")) {
//                            threadCookie.set(); // cookie타입으로 set하고 싶다
                            grinder.logger.info("threadCookie: {}", threadCookie.get().toString())
                        }
                    }
                } else {
                    grinder.logger.warn("No Set-Cookie header found in the response.");
                }
            } catch (Exception e) {
                grinder.logger.error("Error while parsing cookies from response headers.", e);
            }
        }
    }

    @Test
    public void enterQueue() {

        Long boardIdx = 1L // 게시판 ID
        Long userIdx = 1L // 사용자 ID //TODO: JWT TOKEN 디코딩해서 동적으로 가져와야함.
        boolean isAccepted = false;

        // Step 1: 대기열 참여
        HTTPRequest request = new HTTPRequest() // HTTPRequest 객체 생성
        HTTPResponse enterResponse = request.GET("${queueServerAddr}/queue/waiting-room", [
                "boardIdx": boardIdx.toString(),
                "userIdx": userIdx.toString()
        ])

        assertThat(enterResponse.statusCode, is(200)) // 응답 코드 확인

        def enterBody = new JsonSlurper().parseText(enterResponse.getBodyText()) // 응답 본문 파싱
        grinder.logger.info("대기열 참여 응답: {}", enterResponse.getBodyText())

        // 응답 데이터의 'code' 값 확인 (code === 1000인 경우 입장 허가)
        if (enterBody.code == 1000) {
            grinder.logger.info("대기열 참여 완료. 즉시 입장 허가.")
            isAccepted = true;
        }

        // Step 2: 대기 순위 확인 반복
        if (!isAccepted) {
            grinder.logger.info("대기열 참여. 순위 확인 중...") // 순위 확인으로 진행

            while (true) {
                HTTPResponse rankResponse = request.GET("${queueServerAddr}/queue/rank", [
                        "boardIdx": boardIdx.toString(),
                        "userIdx": userIdx.toString()
                ])
                assertThat(rankResponse.statusCode, is(200))

                def rankBody = new JsonSlurper().parseText(rankResponse.getBodyText())
                Long rank = rankBody.result.rank
                grinder.logger.info("현재 순위: {}", rank)

                if (rank <= 1) {
                    grinder.logger.info("대기열 종료. 순위: {}", rank)
                    break
                }

                Thread.sleep(3000) // 3초 대기 후 재시도
            }
        }

        // Step 3: 대기열 종료 후 주문 요청
        List<Cookie> cookies = threadCookies.get();
        request.setHeaders(headers);
        request.addCookies(cookies);

        def registerParams = [
                "boardIdx": 1,
                "orderedProducts": [
                        ["idx": 1, "quantity": 1]
                ]
        ]
        def registerBody = new groovy.json.JsonBuilder(registerParams).toString()
        HTTPResponse registerResponse = request.POST("${logicServerAddr}/orders/register", registerBody.getBytes(), headers)

        // Response Handling
        assertThat(registerResponse.statusCode, is(200)) // 상태 코드 확인
        def responseBody = new JsonSlurper().parseText(registerResponse.getBodyText())
        grinder.logger.info("주문 요청 응답: {}", registerResponse.getBodyText())

        // Check response code and handle result
        if (responseBody.code == 1000) {
            grinder.logger.info("주문 성공: OrderIdx={}", responseBody.result.orderIdx)
        } else {
            grinder.logger.error("주문 실패: {}", responseBody.message)
        }
    }
}
