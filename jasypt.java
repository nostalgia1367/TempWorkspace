<dependency> <groupId>org.jasypt</groupId> <artifactId>jasypt</artifactId> <version>1.9.2</version> </dependency>

public void encryptSimpleTest() { StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor(); encryptor.setPassword("somePassword"); encryptor.setAlgorithm("PBEWithMD5AndDES"); String str = "testString"; String encStr = encryptor.encrypt(str); String decStr = encryptor.decrypt(encStr); log.debug("str : {}, encStr : {}, decStr : {}", str, encStr, decStr); }


암호화 알고리즘 선택
@Test public void getAllAlgorithms() { log.debug("allDigestAlgorithms : {}", AlgorithmRegistry.getAllDigestAlgorithms()); log.debug("allPBEAlgorithms : {}", AlgorithmRegistry.getAllPBEAlgorithms()); }

allDigestAlgorithms : [MD2, MD5, SHA, SHA-224, SHA-256, SHA-384, SHA-512, SHA-512/224, SHA-512/256, SHA3-224, SHA3-256, SHA3-384, SHA3-512] 
allPBEAlgorithms : [PBEWITHHMACSHA1ANDAES_128, PBEWITHHMACSHA1ANDAES_256, PBEWITHHMACSHA224ANDAES_128, PBEWITHHMACSHA224ANDAES_256, PBEWITHHMACSHA256ANDAES_128, PBEWITHHMACSHA256ANDAES_256, PBEWITHHMACSHA384ANDAES_128, PBEWITHHMACSHA384ANDAES_256, PBEWITHHMACSHA512ANDAES_128, PBEWITHHMACSHA512ANDAES_256, PBEWITHMD5ANDDES, PBEWITHMD5ANDTRIPLEDES, PBEWITHSHA1ANDDESEDE, PBEWITHSHA1ANDRC2_128, PBEWITHSHA1ANDRC2_40, PBEWITHSHA1ANDRC4_128, PBEWITHSHA1ANDRC4_40]



@Test public void checkSupportAlgorithm() { List<Object> supported = new ArrayList<>(); List<Object> unsupported = new ArrayList<>(); for (Object algorithm : AlgorithmRegistry.getAllPBEAlgorithms()) { try { StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor(); encryptor.setPassword("somePassword"); encryptor.setAlgorithm(String.valueOf(algorithm)); String str = "test"; String encStr = encryptor.encrypt(str); String decStr = encryptor.decrypt(encStr); assertThat(str.equals(decStr)); supported.add(algorithm); } catch (EncryptionOperationNotPossibleException e) { unsupported.add(algorithm); } } log.debug("supported : {}", supported); log.debug("unsupported : {}", unsupported); }




AES

Security.addProvider(new BouncyCastleProvider()); PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor(); encryptor.setPassword("somePassword"); encryptor.setAlgorithm("PBEWITHSHA256AND128BITAES-CBC-BC");

PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor(); encryptor.setProvider(new BouncyCastleProvider()); encryptor.setPassword("somePassword"); encryptor.setAlgorithm("PBEWITHSHA256AND128BITAES-CBC-BC");







<dependency>
    <groupId>org.jasypt</groupId>
    <artifactId>jasypt-spring31</artifactId>
    <version>1.9.2</version>
    <scope>compile</scope>
</dependency>



스프링XML파일
<!-- Jasypt -->
<bean id="environmentVariablesConfiguration" class="org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig">
	<property name="algorithm" value="PBEWithMD5AndDES" />
</bean>

<bean id="configurationEncryptor" class="org.jasypt.encryption.pbe.StandardPBEStringEncryptor">
	<property name="config" ref="environmentVariablesConfiguration" />
	<property name="password" value="EASTGLOW_PASS" />
</bean>

<bean id="propertyConfigurer" class="org.jasypt.spring31.properties.EncryptablePropertyPlaceholderConfigurer">
	<constructor-arg ref="configurationEncryptor" />
	<property name="locations">
		<list>
			<value>/WEB-INF/props/jdbc.properties</value>
		</list>
	</property>
</bean>


암호화를 위한 Java Class 생성
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class JasyptUtil {

	public void init(){
		    StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword("EASTGLOW_PASS");

        String url = pbeEnc.encrypt("jdbc:oracle:thin:@1.1.1.1:1521:XE");
        String username = pbeEnc.encrypt("USERNAME");
        String password = pbeEnc.encrypt("PASSWORD");

        System.out.println(url);
        System.out.println(username);
        System.out.println(password);
	}
}












또다른 소스
@Configuration public class JasyptConfig { private static final String ENCRYPT_KEY = "toma"; @Bean("jasyptStringEncrptor") public StringEncryptor stringEncryptor() { PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor(); SimpleStringPBEConfig config = new SimpleStringPBEConfig(); config.setPassword(ENCRYPT_KEY); config.setAlgorithm("PBEWithMD5AndDES"); config.setKeyObtentionIterations("1000"); config.setPoolSize("1"); config.setProviderName("toma"); config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); config.setStringOutputType("base64"); encryptor.setConfig(config); return encryptor; } }

출처: https://toma0912.tistory.com/82 [토마의 개발노트]









































properties 파일을 읽어 들이고 저장하기


1. properties 파일 작성

     Properties prop = new Properties();

     prop.setProperty(“SERVER_IP”, “127.0.0.1″);

     prop.setProperty(“SERVER_PORT”, “5000″);

     try{

          OutputStream stream = new FileOutputStream(“파일명”);

          prop.store(stream, “Server Info”);

          stream.close();

     } catch(IOException ex){

           ex.printStackTrace();

     }

 

 

2. properties 파일 읽어들이기

     Properties prop = new Properties();

 

     try {

          InputStream stream = new FileInputStream(“파일명”);

          prop.load(stream);

          stream.close();

 

          System.out.println(“SERVER_IP=” + prop.get(“SERVER_IP”));

     } catch(IOException ex){

          ex.printStackTrace();

     }

 

 

3. java 1.5 버전 부터는 XML파일을 properties 파일로 사용할 수 있게 되었다.

     사용법의 차이는 load(), store() 대신 loadFromXML(), storeToXML() 을 사용하면 된다.

 

     [ XML 파일 포맷 ]

 

     <?xml version=”1.0″ encoding=”UTF-8″?>
     <!DOCTYPE properties SYSTEM “http://java.sun.com/dtd/properties.dtd”>
     <properties>
     <comment>Server Info</comment>
     <entry key=”SERVER_IP”>127.0.0.1</entry>
     <entry key=”SERVER_PORT”>5000</entry>
     </properties>

 