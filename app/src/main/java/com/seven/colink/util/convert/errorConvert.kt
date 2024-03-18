package com.seven.colink.util.convert

fun String.convertError() = when (this) {
    "ERROR_CLAIMS_TOO_LARGE" -> "사용자 정보가 너무 많습니다. 정보를 줄이고 다시 시도해 주세요."
    "ERROR_EMAIL_ALREADY_EXISTS" -> "이미 사용 중인 이메일입니다. 다른 이메일을 사용해 주세요."
    "ERROR_ID_TOKEN_EXPIRED" -> "로그인 정보가 만료되었습니다. 다시 로그인해 주세요."
    "ERROR_ID_TOKEN_REVOKED" -> "로그인 정보가 더 이상 유효하지 않습니다. 다시 로그인해 주세요."
    "ERROR_INSUFFICIENT_PERMISSION" -> "요청 권한이 부족합니다. 관리자에게 문의하세요."
    "ERROR_INTERNAL_ERROR" -> "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."
    "ERROR_INVALID_ARGUMENT" -> "입력한 정보가 잘못되었습니다. 입력 내용을 확인해 주세요."
    "ERROR_INVALID_CLAIMS" -> "지정된 사용자 권한 설정이 잘못되었습니다. 다시 확인해 주세요."
    "ERROR_INVALID_CONTINUE_URI" -> "계속 진행할 수 없는 링크입니다. URL을 확인해 주세요."
    "ERROR_INVALID_CREATION_TIME" -> "계정 생성 시간이 잘못되었습니다. 관리자에게 문의하세요."
    "ERROR_INVALID_CREDENTIAL" -> "로그인 정보가 잘못되었습니다. 로그인 정보를 확인해 주세요."
    "ERROR_INVALID_DISABLED_FIELD" -> "계정 활성 상태가 잘못 설정되었습니다. 관리자에게 문의하세요."
    "ERROR_INVALID_DISPLAY_NAME" -> "사용자 이름이 잘못되었습니다. 이름을 확인하고 다시 시도해 주세요."
    "ERROR_INVALID_DYNAMIC_LINK_DOMAIN" -> "동적 링크 도메인 설정이 잘못되었습니다. 관리자에게 문의하세요."
    "ERROR_INVALID_EMAIL" -> "이메일 형식이 잘못되었습니다. 이메일을 확인해 주세요."
    "ERROR_INVALID_EMAIL_VERIFIED" -> "이메일 인증 상태가 잘못되었습니다. 관리자에게 문의하세요."
    "ERROR_INVALID_HASH_ALGORITHM" -> "비밀번호 알고리즘 설정이 잘못되었습니다. 관리자에게 문의하세요."
    "ERROR_INVALID_HASH_BLOCK_SIZE" -> "해시 블록 크기 설정이 잘못되었습니다. 관리자에게 문의하세요."
    "ERROR_INVALID_HASH_DERIVED_KEY_LENGTH" -> "해시 키 길이 설정이 잘못되었습니다. 관리자에게 문의하세요."
    "ERROR_INVALID_HASH_KEY" -> "해시 키 설정이 잘못되었습니다. 관리자에게 문의하세요."
    "ERROR_INVALID_HASH_MEMORY_COST" -> "해시 메모리 비용 설정이 잘못되었습니다. 관리자에게 문의하세요."
    "ERROR_INVALID_HASH_PARALLELIZATION" -> "해시 병렬 처리 설정이 잘못되었습니다. 관리자에게 문의하세요."
    "ERROR_INVALID_HASH_ROUNDS" -> "해시 라운드 설정이 잘못되었습니다. 관리자에게 문의하세요."
    "ERROR_INVALID_HASH_SALT_SEPARATOR" -> "해시 솔트 구분자 설정이 잘못되었습니다. 관리자에게 문의하세요."
    "ERROR_INVALID_ID_TOKEN" -> "ID 토큰이 잘못되었습니다. 다시 로그인해 주세요."
    "ERROR_INVALID_LAST_SIGN_IN_TIME" -> "마지막 로그인 시간 정보가 잘못되었습니다. 관리자에게 문의하세요."
    "ERROR_INVALID_PAGE_TOKEN" -> "페이지 토큰이 잘못되었습니다. 페이지를 새로고침하고 다시 시도해 주세요."
    "ERROR_INVALID_PASSWORD" -> "비밀번호가 잘못되었습니다. 비밀번호를 확인해 주세요."
    "ERROR_INVALID_PASSWORD_HASH" -> "비밀번호 해시 설정이 잘못되었습니다. 관리자에게 문의하세요."
    "ERROR_INVALID_PASSWORD_SALT" -> "비밀번호 솔트 설정이 잘못되었습니다. 관리자에게 문의하세요."
    "ERROR_INVALID_PHONE_NUMBER" -> "전화번호 형식이 잘못되었습니다. 전화번호를 확인해 주세요."
    "ERROR_INVALID_PHOTO_URL" -> "사진 URL이 잘못되었습니다. URL을 확인해 주세요."
    "ERROR_INVALID_PROVIDER_DATA" -> "제공업체 정보 설정이 잘못되었습니다. 관리자에게 문의하세요."
    "ERROR_INVALID_PROVIDER_ID" -> "제공업체 ID가 잘못되었습니다. 관리자에게 문의하세요."
    "ERROR_INVALID_OAUTH_RESPONSETYPE" -> "OAuth 응답 유형 설정이 잘못되었습니다. 관리자에게 문의하세요."
    "ERROR_INVALID_SESSION_COOKIE_DURATION" -> "세션 쿠키 지속 시간이 잘못 설정되었습니다. 관리자에게 문의하세요."
    "ERROR_INVALID_UID" -> "UID 형식이 잘못되었습니다. UID를 확인해 주세요."
    "ERROR_INVALID_USER_IMPORT" -> "사용자 가져오기 정보가 잘못되었습니다. 관리자에게 문의하세요."
    "ERROR_MAXIMUM_USER_COUNT_EXCEEDED" -> "최대 사용자 수를 초과했습니다. 관리자에게 문의하세요."
    "ERROR_MISSING_ANDROID_PKG_NAME" -> "Android 패키지 이름이 필요합니다. 관리자에게 문의하세요."
    "ERROR_MISSING_CONTINUE_URI" -> "계속하기 위한 URL이 필요합니다. URL을 확인해 주세요."
    "ERROR_MISSING_HASH_ALGORITHM" -> "비밀번호 해시 알고리즘이 필요합니다. 관리자에게 문의하세요."
    "ERROR_MISSING_IOS_BUNDLE_ID" -> "iOS 번들 ID가 필요합니다. 관리자에게 문의하세요."
    "ERROR_MISSING_UID" -> "UID가 필요합니다. UID를 확인해 주세요."
    "ERROR_MISSING_OAUTH_CLIENT_SECRET" -> "OAuth 클라이언트 비밀번호가 필요합니다. 관리자에게 문의하세요."
    "ERROR_OPERATION_NOT_ALLOWED" -> "이 작업은 현재 허용되지 않습니다. 설정을 확인해 주세요."
    "ERROR_PHONE_NUMBER_ALREADY_EXISTS" -> "이미 사용 중인 전화번호입니다. 다른 번호를 사용해 주세요."
    "ERROR_PROJECT_NOT_FOUND" -> "프로젝트를 찾을 수 없습니다. 프로젝트 설정을 확인해 주세요."
    "ERROR_RESERVED_CLAIMS" -> "일부 클레임은 시스템에서 예약되어 있습니다. 다른 클레임을 사용해 주세요."
    "ERROR_SESSION_COOKIE_EXPIRED" -> "세션 쿠키가 만료되었습니다. 다시 로그인해 주세요."
    "ERROR_SESSION_COOKIE_REVOKED" -> "세션 쿠키가 취소되었습니다. 다시 로그인해 주세요."
    "ERROR_TOO_MANY_REQUESTS" -> "요청이 너무 많습니다. 잠시 후 다시 시도해 주세요."
    "ERROR_UID_ALREADY_EXISTS" -> "제공된 UID를 이미 사용 중인 사용자가 있습니다. 다른 UID를 사용해 주세요."
    "ERROR_UNAUTHORIZED_CONTINUE_URI" -> "계속하기 위한 URL이 승인되지 않았습니다. URL을 확인하거나 승인해 주세요."
    "ERROR_USER_NOT_FOUND" -> "사용자를 찾을 수 없습니다. 정보를 확인하고 다시 시도해 주세요."
    else -> "알수없는 에러"

//        "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."
}

