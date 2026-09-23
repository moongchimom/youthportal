package kr.or.oti.youthportal.dto; // DTO(데이터 전달 객체) 패키지

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data // getter/setter 등 일괄 생성
public class UploadFileDTO { // 파일 업로드 요청(폼 입력)을 담는 DTO - 업로드 기능이 필요한 어떤 화면에서도 재사용 가능
	private List<MultipartFile> files; // 업로드할 파일 목록 (폼 입력명 "files"와 일치해야 바인딩됨)
}
