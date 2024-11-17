package org.example.backend.global.common.constants;

public class SwaggerDescription {
    //업체회원가입 설명 틀
    public static final String COMPANY_SIGNUP_REQUEST =
            "먼저 이메일 인증 요청 후 회원가입 요청을 진행해주세요\n" +
            "테스트 가능한 사업자 등록정보:\n" +
            "- 번호: 1208800767\n" +
            "- 회원이름: 강한승\n" +
            "- 개업일자: 20130716";
    //일반회원가입 설명 틀
    public static final String USER_SIGNUP_REQUEST = "먼저 이메일 인증 요청 후 회원가입 요청을 진행해주세요";
    public static final String ORDERS_REGISTER_REQUEST = "주문을 등록하는 API 입니다. 먼저 게시글과 상품을 만들고 진행해주세요.";

    public static final String ORDERS_COMPLETE_REQUEST = "주문을 완료하는 API 입니다. 먼저 주문 등록을 진행해주세요.";

    //이메일 인증 설명 틀
    public static final String EMAIL_AUTH_REQUEST = "이메일을 입력하면 인증코드를 생성하여 이메일로 발송합니다. 만료기한은 10분입니다.";
    public static final String COMPANY_PRO_BRD_LIST = "page는 1부터 입력해주세요<br>" +
        "staus/month에 아무 값도 넣지 않으면 전체 조회입니다<br>\n" +
        "- status: [진행 전, 진행 중, 진행 완료]<br>\n" +
        "- month: [1, 3, 6, 12]";
    //문의 등록 설명 틀
    public static final String QNA_QUESTION_REQUEST = "문의 제목과 내용을 입력해주세요.";
}
