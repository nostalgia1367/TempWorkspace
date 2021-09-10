import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		String dir = "D:/TEST";
		WatchService service = FileSystems.getDefault().newWatchService();
		Path path = Paths.get(dir);
		path.register(service, 
				StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_DELETE,
				StandardWatchEventKinds.ENTRY_MODIFY);
		while(true) {
			WatchKey key = service.take();
			List<WatchEvent<?>> list = key.pollEvents(); //이벤트를 받을 때까지 대기
			for(WatchEvent<?> event : list) {
				Kind<?> kind = event.kind();
				Path pth = (Path) event.context();
				if(kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
					//파일이 생성되었을 때 실행되는 코드
					System.out.println("생성 : " + pth.getFileName());
				} else if(kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
					//파일이 삭제되었을 때 실행되는 코드
					System.out.println("삭제 : " + pth.getFileName());
				} else if(kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
					//파일이 수정되었을 때 실행되는 코드
					System.out.println("수정 : " + pth.getFileName());
				} else if(kind.equals(StandardWatchEventKinds.OVERFLOW)) {
					//운영체제에서 이벤트가 소실되었거나 버려질 경우 실행되는 코드
					System.out.println("OVERFLOW");
				}
			}
			if(!key.reset()) break; //키 리셋
		}
		service.close();
	}
}

//파일 변경 감지
//https://m.blog.naver.com/wpdus2694/221677886304


/*

우선 WatchService 객체를 생성하면서 시작한다.

WatchService service = FileSystems.getDefault().newWatchService();
그리고 해당 경로를 WatchService를 생성,삭제,수정에 대하여 반응할 수 있도록 등록한다.

Path path = Paths.get(dir);
path.register(service, 
		StandardWatchEventKinds.ENTRY_CREATE,
		StandardWatchEventKinds.ENTRY_DELETE,
		StandardWatchEventKinds.ENTRY_MODIFY);
그리고 해당 서비스에 대하여 key값을 받아온다.

WatchKey key = service.take();
key값을 이용하여 이벤트를 받아올 수 있다.

단, 이벤트가 발생되어 이벤트값을 얻어오기 전까지는 해당 쓰레드는 일시정지된다.

List<WatchEvent<?>> list = key.pollEvents();
이벤트를 받아왔다면 for문으로 각 이벤트마다 반응할 수 있도록 작성한다.

ENTRY_CREATE : 생성

ENTRY_DELETE : 삭제

ENTRY_MODIFY : 수정

OVERFLOW : 운영체제에서 이벤트가 소실되거나 버려짐

pth 변수는 변화가 일어난 파일을 가리킨다.

for(WatchEvent<?> event : list) {
	Kind<?> kind = event.kind();
	Path pth = (Path) event.context();
	if(kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
		System.out.println("생성 : " + pth.getFileName());
	} else if(kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
		System.out.println("삭제 : " + pth.getFileName());
	} else if(kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
		System.out.println("수정 : " + pth.getFileName());
	} else if(kind.equals(StandardWatchEventKinds.OVERFLOW)) {
		System.out.println("OVERFLOW");
	}
}
키 값은 한번 사용 후 초기화하여서 다시 사용해야한다.

if(!key.reset()) break;
key.reset() 이라는 메소드를 이용하여 초기화할 수 있는데, 해당 메소드에서 true가 반환된다면 성공적으로 키를 초기화한 것이다.

만약 감시하는 디렉토리가 삭제되거나 예외가 발생하여 key가 유효하지 않게 된다면 false를 반환한다. 따라서 key가 유효하지 않게 되면 

while문을 빠져나오도록 작성이 됐다.

service.close();

*/