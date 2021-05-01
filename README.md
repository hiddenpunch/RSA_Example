1. 개발언어: Java

2. 사전조건
    2.1. OpenJDK 다운로드 및 설치: https://openjdk.java.net/install/index.html
    2.2. Windows 환경변수 설정: Windows 설정(Win + i) > 시스템 > 정보 > 시스템 정보 > 고급 시스템 설정 > 환경변수
                                > 'Path’ 선택 후 편집 >OpenJDK 설치 경로 추가
    2.3. 설치 및 환경변수 설정 확인: Win + R > ‘cmd’ 입력 > 콘솔창에서 ‘java –version’ 입력 > 버전 출력 확인
    2.4. 소스코드 컴파일
         2.4.1. ..\alice> javac RsaEncDec.java
         2.4.2. ..\bob> javac RsaEncDec.java
    2.5.  ..\bob> secret.txt에 미리 원하는 plain text를 작성
    2.6. bob과 alice폴더 사이에서의 이동 : ..\bob> cd ..\alice\
				 ..\alice> cd ..\bob\

3. 시나리오 구현
    3.1. Alice, RSA KeyPair(PKCS12 표준) 생성(툴)
         3.1.1. ..\alice> keytool -genkeypair -rfc -keyalg rsa -keysize 2048 -keystore aliceKeystore.p12 -storetype pkcs12 -storepass hidpun25 -validity 365 -alias alice -dname CN=alice
    3.2. Alice, 생성된 PKCS12 에서 X.509 형식으로 public key 추출
         3.2.1. ..\alice> keytool -exportcert -rfc -keystore aliceKeystore.p12 -storetype pkcs12 -storepass hidpun25 -alias alice -file alice.crt
    3.3. Alice, X.509 를 Bob 에게 전달
         3.3.1. ..\alice> copy alice.crt ..\bob\
    3.4. Bob, Alice 의 공개키로 평문 파일을 RSA 암호화
         3.4.1. ..\bob>java RsaEncDec enc alice.crt secret.txt
    3.5. Bob, 암호화된 메시지를 Alice 에게 전달
         3.5.1. ..\bob>copy RsaEncryptedOutput ..\alice\
    3.6. Alice, 암호화된 메시지 복호화 및 확인
         3.6.1. ..\alice> java RsaEncDec dec aliceKeystore.p12 RsaEncryptedOutput
         3.6.2. ..\alice> type RsaDecryptedOutput