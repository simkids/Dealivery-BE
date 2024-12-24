package org.example.backend.global.common.constants;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    // 모든 요청 성공 1000
    SUCCESS(true, 1000, "요청이 성공하였습니다."),

    // 회원 기능 2000
    USER_SIGNUP_FAIL(false, 2000, "회원가입에 실패했습니다."),
    USER_SIGNUP_FAIL_EMPTY_NAME(false, 2001, "회원가입에 실패했습니다. 이름을 입력해주세요."),
    USER_SIGNUP_FAIL_EMPTY_EMAIL(false, 2002, "회원가입에 실패했습니다. 이메일을 입력해주세요."),
    USER_SIGNUP_FAIL_EMPTY_PASSWORD(false, 2003, "회원가입에 실패했습니다. 비밀번호를 입력해주세요."),
    USER_SIGNUP_FAIL_EMPTY_PHONE_NUMBER(false, 2004, "회원가입에 실패했습니다. 휴대폰 번호를 입력해주세요."),
    USER_SIGNUP_FAIL_EMPTY_POST_NUMBER(false, 2005, "회원가입에 실패했습니다. 우편번호를 입력해주세요."),
    USER_SIGNUP_FAIL_EMPTY_ADDRESS_DETAIL(false, 2006, "회원가입에 실패했습니다. 상세주소를 입력해주세요."),
    USER_SIGNUP_FAIL_INVALID_NAME(false, 2007, "회원가입에 실패했습니다. 유효한 이름을 입력해주세요."),
    USER_SIGNUP_FAIL_INVALID_EMAIL(false, 2008, "회원가입에 실패했습니다. 유효한 이메일을 입력해주세요."),
    USER_SIGNUP_FAIL_INVALID_PASSWORD(false, 2009, "회원가입에 실패했습니다. 유효한 비밀번호를 입력해주세요."),
    USER_SIGNUP_FAIL_INVALID_PHONE_NUMBER(false, 2010, "회원가입에 실패했습니다. 유효한 휴대폰 번호를 입력해주세요."),
    USER_SIGNUP_FAIL_INVALID_POST_NUMBER(false, 2011, "회원가입에 실패했습니다. 유효한 우편번호를 입력해주세요."),
    USER_SIGNUP_FAIL_INVALID_ADDRESS_DETAIL(false, 2012, "회원가입에 실패했습니다. 유효한 상세주소를 입력해주세요."),
    USER_SIGNUP_FAIL_INVALID_EMAIL_CODE(false, 2058, "회원가입에 실패했습니다. 이메일 인증 코드가 만료되었거나 일치하지 않습니다."),

    USER_LOGIN_FAIL(false, 2013, "로그인에 실패했습니다. 아이디 또는 비밀번호를 확인해주세요."),
    USER_LOGIN_FAIL_EMPTY_EMAIL(false, 2014, "로그인에 실패했습니다. 아이디를 입력해주세요."),
    USER_LOGIN_FAIL_EMPTY_PASSWORD(false, 2015, "로그인에 실패했습니다. 비밀번호를 입력해주세요."),
    USER_LOGIN_FAIL_UNAUTHORIZED_EMAIL(false, 2016, "로그인에 실패했습니다. 인증되지 않은 이메일입니다."),

    USER_EDIT_FAIL_INVALID_NAME(false, 2017, "회원정보 수정에 실패했습니다. 유효한 이름을 입력해주세요."),
    USER_EDIT_FAIL_INVALID_PASSWORD(false, 2018, "회원정보 수정에 실패했습니다. 유효한 비밀번호를 입력해주세요."),
    USER_EDIT_FAIL_INVALID_PHONE_NUMBER(false, 2019, "회원정보 수정에 실패했습니다. 유효한 휴대폰 번호를 입력해주세요."),
    USER_EDIT_FAIL_INVALID_POST_NUMBER(false, 2020, "회원정보 수정에 실패했습니다. 유효한 우편번호를 입력해주세요."),
    USER_EDIT_FAIL_INVALID_ADDRESS(false, 2021, "회원정보 수정에 실패했습니다. 유효한 주소를 입력해주세요."),
    USER_EDIT_FAIL_INVALID_ADDRESS_DETAIL(false, 2022, "회원정보 수정에 실패했습니다. 유효한 상세주소를 입력해주세요."),

    USER_FIND_ID_FAIL_NO_MATCH_EMAIL(false, 2023, "아이디 찾기에 실패했습니다. 일치하는 아이디가 없습니다."),
    USER_FIND_ID_FAIL_EMPTY_NAME(false, 2024, "아이디 찾기에 실패했습니다. 이름을 입력해주세요."),
    USER_FIND_ID_FAIL_EMPTY_PHONE_NUMBER(false, 2025, "아이디 찾기에 실패했습니다. 휴대폰 번호를 입력해주세요."),
    USER_FIND_PASSWORD_FAIL(false, 2026, "비밀번호 찾기에 실패했습니다."),
    USER_RESET_PASSWORD_FAIL(false, 2056, "비밀번호 재설정에 실패했습니다."),
    USER_FIND_PASSWORD_FAIL_EMAIL_VERIFY_FAIL(false, 2027, "비밀번호 찾기에 실패했습니다. 입력하신 정보와 일치하는 회원정보가 없습니다."),

    USER_DELIVERY_ADD_FAIL_INVALID_NAME(false, 2028, "배송지 추가에 실패했습니다. 유효한 이름을 입력해주세요."),
    USER_DELIVERY_ADD_FAIL_INVALID_ADDRESS(false, 2029, "배송지 추가에 실패했습니다. 유효한 주소를 입력해주세요."),
    USER_DELIVERY_ADD_FAIL_INVALID_ADDRESS_DETAIL(false, 2030, "배송지 추가에 실패했습니다. 유효한 상세주소를 입력해주세요."),
    USER_DELIVERY_ADD_FAIL_INVALID_POST_NUMBER(false, 2031, "배송지 추가에 실패했습니다. 유효한 우편번호를 입력해주세요."),
    USER_DELIVERY_ADD_FAIL_INVALID_RECEIVER_NAME(false, 2032, "배송지 추가에 실패했습니다. 유효한 수취인명을 입력해주세요."),
    USER_DELIVERY_ADD_FAIL_INVALID_RECEIVER_PHONE_NUMBER(false, 2033, "배송지 추가에 실패했습니다. 유효한 수취인연락처를 입력해주세요."),
    USER_DELIVERY_ADD_FAIL_EMPTY_NAME(false, 2034, "배송지 추가에 실패했습니다. 이름을 입력해주세요."),
    USER_DELIVERY_ADD_FAIL_EMPTY_ADDRESS(false, 2035, "배송지 추가에 실패했습니다. 주소를 입력해주세요."),
    USER_DELIVERY_ADD_FAIL_EMPTY_ADDRESS_DETAIL(false, 2036, "배송지 추가에 실패했습니다. 상세주소를 입력해주세요."),
    USER_DELIVERY_ADD_FAIL_EMPTY_POST_NUMBER(false, 2037, "배송지 추가에 실패했습니다. 우편번호를 입력해주세요."),
    USER_DELIVERY_ADD_FAIL_EMPTY_RECEIVER_NAME(false, 2038, "배송지 추가에 실패했습니다. 수취인명을 입력해주세요."),
    USER_DELIVERY_ADD_FAIL_EMPTY_RECEIVER_PHONE_NUMBER(false, 2039, "배송지 추가에 실패했습니다. 수취인연락처를 입력해주세요."),
    USER_DELIVERY_ADD_FAIL_EXCEEDANCE_MAX_DELIVERY(false, 2040, "배송지는 10개까지 등록 가능합니다."),
    USER_DELIVERY_REMOVE_FAIL_NO_MATCH_DELIVERY(false, 2041, "배송지 삭제에 실패했습니다. 일치하는 배송지가 없습니다."),
    USER_DELIVERY_REMOVE_FAIL_EMPTY_DELIVERY(false, 2042, "배송지 삭제에 실패했습니다. 등록된 배송지가 없습니다."),
    USER_DELIVERY_LIST_FAIL(false, 2054, "배송지 목록조회에 실패했습니다."),
    USER_DELIVERY_EDIT_FAIL(false, 2055, "배송지 정보수정에 실패했습니다."),

    USER_SIGNUP_FAIL_INVALID_REG_NUMBER(false, 2043, "회원가입에 실패했습니다. 유효한 사업자등록번호를 입력해주세요."),
    USER_SIGNUP_FAIL_INVALID_OPENED_AT(false, 2044, "회원가입에 실패했습니다. 유효한 개업일자를 입력해주세요."),
    USER_SIGNUP_FAIL_INVALID_MOS_NUMBER(false, 2045, "회원가입에 실패했습니다. 유효한 통신판매업신고번호를 입력해주세요."),
    USER_SIGNUP_FAIL_EMPTY_REG_NUMBER(false, 2046, "회원가입에 실패했습니다. 사업자등록번호를 입력해주세요."),
    USER_SIGNUP_FAIL_EMPTY_OPENED_AT(false, 2047, "회원가입에 실패했습니다. 개업일자를 입력해주세요."),
    USER_SIGNUP_FAIL_EMPTY_MOS_NUMBER(false, 2048, "회원가입에 실패했습니다. 통신판매업신고번호를 입력해주세요."),
    USER_SIGNUP_FAIL_UNAUTHORIZED_REG_NUMBER(false, 2049, "사업자등록번호 인증에 실패했습니다."),
    USER_SIGNUP_FAIL_UNAUTHORIZED_MOS_NUMBER(false, 2050, "통신판매업신호번호 인증에 실패했습니다."),

    EMAIL_VERIFY_FAIL(false, 2051, "이메일 인증요청에 실패했습니다."),
    USER_DETAIL_FAIL(false, 2052, "회원정보 조회에 실패했습니다."),
    USER_REMOVE_FAIL(false, 2053, "회원탈퇴에 실패했습니다."),
    USER_SOCIAL_LOGIN_FAIL_NEED_TO_SIGNUP(true, 2057, "추가 회원정보 입력이 필요합니다."),
    EMAIL_VERIFY_FAIL_EXPIRED(false, 2059, "이메일 인증에 실패했습니다. 인증코드가 만료되었습니다."),
    EMAIL_VERIFY_FAIL_INCORRECT(false, 2060, "이메일 인증에 실패했습니다. 인증코드가 일치하지 않습니다."),
    EMAIL_VERIFY_FAIL_CAN_NOT_CREATE(false, 2061, "인증코드를 생성할 수 없습니다. 이메일을 확인해주세요."),
    EMAIL_VERIFY_FAIL_CAN_NOT_SEND(false, 2061, "인증메일 발송에 실패했습니다."),
    USER_SIGNUP_FAIL_ALREADY_EXIST(false, 2062, "이미 가입된 이메일입니다."),
    USER_LOGIN_FAIL_UNAUTHORIZED_REG_NUMBER(false, 2063, "로그인에 실패했습니다. 사업자등록번호 인증 정보가 없는 회원입니다."),
    USER_DETAIL_FAIL_USER_NOT_FOUND(false, 2064, "회원정보 조회에 실패했습니다. 해당 정보로 가입된 회원정보가 없습니다."),
    USER_DELIVERY_EDIT_FAIL_USER_NOT_MATCH(false, 2065, "배송지 정보수정에 실패했습니다. 회원 정보가 올바르지 않습니다."),
    USER_DETAIL_EDIT_FAIL_USER_NOT_FOUND(false, 2066, "회원정보 수정에 실패했습니다. 없는 회원입니다."),


    // 주문 기능 3000
    ORDER_FAIL_LIST(false, 3001, "주문내역 조회에 실패했습니다."),
    ORDER_CANCEL_FAIL(false, 3002, "주문취소에 실패했습니다."),
    ORDER_PAYMENT_FAIL(false, 3003, "결제에 실패했습니다. 주문 실패 처리됩니다."),
    ORDER_CREATE_FAIL_LACK_STOCK(false, 3004, "주문에 실패했습니다. 상품재고가 부족합니다."),
    ORDER_CANCEL_FAIL_KAKAO_PAY(false, 3005, "주문에 실패했습니다. 카카오페이 결제 실패했습니다."),
    ORDER_VALIDATION_FAIL_PRICE_MISMATCH(false, 3006, "주문에 실패했습니다. 주문 정보와 결제금액이 일치하지 않아 결제취소됩니다"),
    ORDER_CANCEL_FAIL_ALREADY_CANCELD(false, 3007, "주문 취소에 실패했습니다. 이미 환불완료된 주문입니다"),
    ORDER_FAIL_NONIMPUID(false, 3008, "주문에 실패했습니다. 결제여부를 확인해 주세요"),
    ORDER_EDIT_FAIL(false, 3009, "주문정보 수정에 실패했습니다."),
    ORDER_FAIL_DETAIL(false, 3010, "주문 상세조회에 실패했습니다."),
    ORDER_FAIL_EVENT_NOT_FOUND(false, 3011, "주문에 실패했습니다. 해당하든 게시글을 찾을 수 없습니다."),
    ORDER_FAIL_NOT_FOUND(false, 3012, "해당하는 주문을 찾을 수 없습니다."),
    ORDER_FAIL_INVALID_PRODUCT_PRICE(false, 3013, "주문에 실패했습니다. 해당하는 상품의 가격 정보가 유효하지 않습니다."),
    ORDER_FAIL_EXPIRED_EVENT(false, 3014, "주문에 실패했습니다. 종료된 이벤트입니다."),
    ORDER_FAIL_PRODUCT_NOT_FOUND(false, 3015, "주문에 실패했습니다. 해당하는 상품을 찾을 수 없습니다."),
    ORDER_FAIL_PAYMENT_NOT_FOUND(false, 3016, "주문에 실패했습니다. 결제 정보를 찾을 수 없습니다."),
    ORDER_PAYMENT_CANCEL_FAILED(false, 3017,"결제 취소 요청 중 오류가 발생했습니다. 관리자에게 문의해주세요."),
    ORDER_FAIL_UNOPENED_EVENT(false, 3018, "주문에 실패했습니다. 오픈 예정인 이벤트입니다."),

    // 상품게시글 기능 4000
    PRODUCT_BOARD_REGISTER_FAIL_EMPTY_TITLE(false, 4000, "상품게시글 등록에 실패했습니다. 제목을 입력해주세요"),
    PRODUCT_BOARD_REGISTER_FAIL_INVALID_TITLE(false, 4001, "상품게시글 등록에 실패했습니다. 유효한 제목을 입력해주세요"),
    PRODUCT_BOARD_REGISTER_FAIL_EMPTY_IMAGE(false, 4002, "상품게시글 등록에 실패했습니다. 상품 이미지를 1장이상 등록해주세요"),
    PRODUCT_BOARD_REGISTER_FAIL_UPLOAD_IMAGE(false, 4003, "상품 이미지 파일 업로드를 실패했습니다"),
    PRODUCT_BOARD_REGISTER_FAIL_EMPTY_START_TIME(false, 4010, "상품게시글 등록에 실패했습니다. 상품 판매 시작 시간을 입력해주세요"),
    PRODUCT_BOARD_REGISTER_FAIL_EMPTY_END_TIME(false, 4011, "상품게시글 등록에 실패했습니다. 상품 판매 종료 시간을 입력해주세요"),
    PRODUCT_BOARD_REGISTER_FAIL_INVALID_START_TIME(false, 4012, "상품게시글 등록에 실패했습니다. 유효한 시작 시간이 아닙니다."),
    PRODUCT_BOARD_REGISTER_FAIL_INVALID_END_TIME(false, 4013, "상품게시글 등록에 실패했습니다. 유효한 종료 시간이 아닙니다."),
    PRODUCT_BOARD_REGISTER_FAIL_INVALID_MINUTE(false, 4014, "상품게시글 등록에 실패했습니다. 유효한 시간(minute)이 아닙니다."),
    PRODUCT_BOARD_REGISTER_FAIL_EMPTY_PRODUCT_NAME(false, 4020, "상품게시글 등록에 실패했습니다. 상품 이름을 입력해주세요"),
    PRODUCT_BOARD_REGISTER_FAIL_EMPTY_STOCK(false, 4021, "상품게시글 등록에 실패했습니다. 상품 재고를 입력해주세요"),
    PRODUCT_BOARD_REGISTER_FAIL_EMPTY_PRICE(false, 4022, "상품게시글 등록에 실패했습니다. 상품 가격을 입력해주세요"),
    PRODUCT_BOARD_REGISTER_FAIL_INVALID_PRODUCT_NAME(false, 4023, "상품게시글 등록에 실패했습니다. 유효한 상품 이름을 입력해주세요"),
    PRODUCT_BOARD_REGISTER_FAIL_INVALID_PRODUCT_STOCK(false, 4024, "상품게시글 등록에 실패했습니다. 유효한 상품 재고값을 입력해주세요"),
    PRODUCT_BOARD_REGISTER_FAIL_INVALID_PRODUCT_PRICE(false, 4025, "상품게시글 등록에 실패했습니다. 유효한 상품 가격을 입력해주세요"),
    PRODUCT_BOARD_REGISTER_FAIL_EMPTY_CATEGORY(false, 4030, "상품게시글 등록에 실패했습니다. 카테고리를 입력해주세요"),
    PRODUCT_BOARD_REGISTER_FAIL_INVALID_CATEGORY(false, 4031, "상품게시글 등록에 실패했습니다. 유효한 카테고리가 아닙니다. 다시 입력해주세요"),
    PRODUCT_BOARD_REGISTER_FAIL_START_TIME_PASSED(false, 4040, "상품게시글 등록에 실패했습니다. 시작 시간이 이미 지나 게시글을 수정할 수 없습니다."),
    PRODUCT_BOARD_REMOVE_FAIL_START_TIME_PASSED(false, 4050, "상품게시글 삭제에 실패했습니다. 시작 시간이 이미 지나 게시글을 삭제할 수 없습니다."),
    PRODUCT_BOARD_LIST_FAIL(false, 4100, "상품게시글 조회에 실패했습니다."),
    PRODUCT_BOARD_DETAIL_FAIL(false, 4200, "상품 상세 조회에 실패했습니다."),
    PRODUCT_BOARD_QUEUE_CREATE_FAIL(false, 4300, "대기열 생성에 실패했습니다."),

    // 관심 기능 5000
    LIKES_REGISTER_FAIL(false, 5000, "관심게시글 등록에 실패했습니다."),
    LIKES_REGISTER_FAIL_NO_MATCH_PRODUCT_BOARD(false, 5001, "관심게시글 등록에 실패했습니다. 해당 상품게시글이 존재하지 않습니다."),
    LIKES_CANCLE_FAIL(false, 5002, "관심게시글 취소에 실패했습니다."),
    LIKES_CANCLE_FAIL_NO_MATCH_PRODUCT_BOARD(false, 5003, "관심게시글 취소에 실패했습니다. 해당 상품게시글이 존재하지 않습니다."),
    LIKES_CANCLE_FAIL_EXCEED_MAX_LIKES(false, 5004, "관심게시글 등록/취소에 실패했습니다. 최대 100개까지 등록 가능합니다."),

    // 문의 기능 6000
    QNA_QUESTION_FAIL(false, 6000, "문의 등록에 실패했습니다."),
    QNA_QUESTION_FAIL_EMPTY_TITLE(false, 6001, "문의 등록에 실패했습니다. 제목을 입력해주세요."),
    QNA_QUESTION_FAIL_EMPTY_CONTENT(false, 6002, "문의 등록에 실패했습니다. 내용을 입력해주세요."),
    QNA_QUESTION_FAIL_TOO_LONG_TITLE(false, 6003, "문의 등록에 실패했습니다. 제목은 최대 50자까지 입력 가능합니다."),
    QNA_QUESTION_FAIL_TOO_LONG_CONTENT(false, 6004, "문의 등록에 실패했습니다. 내용은 최대 255자까지 입력 가능합니다."),

    QNA_QUESTION_UPDATE_FAIL(false, 6005, "문의 수정에 실패했습니다."),
    QNA_QUESTION_UPDATE_FAIL_NOT_FOUND(false, 6006, "문의 수정에 실패했습니다. 해당 문의를 찾을 수 없습니다."),

    QNA_QUESTION_DELETE_FAIL(false, 6007, "문의 삭제에 실패했습니다."),
    QNA_QUESTION_DELETE_FAIL_NOT_FOUND(false, 6008, "문의 삭제에 실패했습니다. 해당 문의를 찾을 수 없습니다."),

    QNA_ANSWERS_FAIL(false, 6009, "답변 등록에 실패했습니다."),
    QNA_ANSWERS_FAIL_EMPTY_CONTENT(false, 6010, "답변 등록에 실패했습니다. 내용을 입력해주세요."),
    QNA_ANSWER_FAIL_ALREADY_EXISTS(false, 6011, "답변 등록에 실패했습니다. 이미 답변이 등록된 문의입니다."),
    QNA_ANSWER_FAIL_NOT_FOUND(false, 6012, "답변 등록에 실패했습니다. 해당 문의를 찾을 수 없습니다."),
    QNA_ANSWER_FAIL_TOO_LONG_CONTENT(false, 6013, "답변 등록에 실패했습니다. 내용은 최대 255자까지 입력 가능합니다."),

    QNA_ANSWER_UPDATE_FAIL(false, 6014, "답변 수정에 실패했습니다."),
    QNA_ANSWER_UPDATE_FAIL_NOT_FOUND(false, 6015, "답변 수정에 실패했습니다. 해당 답변을 찾을 수 없습니다."),

    QNA_ANSWER_DELETE_FAIL(false, 6016, "답변 삭제에 실패했습니다."),
    QNA_ANSWER_DELETE_FAIL_NOT_FOUND(false, 6017, "답변 삭제에 실패했습니다. 해당 답변을 찾을 수 없습니다."),

    QNA_QUESTION_LIST_FAIL(false, 6018, "문의 목록 조회에 실패했습니다."),
    QNA_QUESTION_LIST_FAIL_INVALID_PRODUCT_BOARD_IDX(false, 6019, "문의 목록 조회에 실패했습니다. 잘못된 상품 게시글 ID입니다."),

    QNA_USER_NOT_FOUND(false, 6020, "사용자를 찾을 수 없습니다."),
    QNA_PRODUCT_BOARD_NOT_FOUND(false, 6021, "상품 게시글을 찾을 수 없습니다."),


    // 모든 요청 실패 9000
    FAIL(false, 9000, "요청에 실패했습니다. 관리자에게 문의해주세요."),
    FAIL_UNAUTHORIZED(false, 9001, "요청이 실패했습니다. 권한이 없습니다.");

    private boolean isSuccess;
    private Integer code;
    private String message;

    BaseResponseStatus(Boolean isSuccess, Integer code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
     }
    }
