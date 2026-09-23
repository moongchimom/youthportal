package kr.or.oti.youthportal.controller; // 컨트롤러(웹 요청 처리) 패키지

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import kr.or.oti.youthportal.dto.UploadFileDTO;
import kr.or.oti.youthportal.dto.UploadResultDTO;

// 파일 업로드/저장 책임만 전담하는 공용 컨트롤러 - ApplicationController 등 업로드가 필요한 화면에서 재사용
@Controller // 스프링이 이 클래스를 웹 컨트롤러 빈으로 등록
@RequestMapping("/updown") // /updown으로 시작하는 요청을 이 컨트롤러가 처리
public class UpDownController {

	@Value("${app.upload-dir}") // application.properties에 정의한 업로드 저장 경로 주입
	private String uploadDir;

	// 파일 업로드 처리 - 여러 개의 파일을 저장하고, 각각의 저장 결과(저장명/원본명) 목록을 반환
	@PostMapping("/upload")
	@ResponseBody // 뷰 이름이 아니라 반환 객체 자체를 JSON으로 내려줌
	public List<UploadResultDTO> upload(UploadFileDTO uploadFileDTO) {

		List<UploadResultDTO> resultList = new ArrayList<>(); // 저장 성공한 파일들의 결과를 담을 리스트

		if (uploadFileDTO.getFiles() != null) { // 업로드된 파일이 하나 이상 있다면
			for (MultipartFile file : uploadFileDTO.getFiles()) { // 파일마다 반복
				if (file == null || file.isEmpty()) { // 빈 입력(선택 안 한 칸)은 건너뜀
					continue;
				}
				try {
					resultList.add(new UploadResultDTO(uploadDir, file)); // 생성자에서 실제 저장까지 수행됨
				} catch (IOException e) { // 저장 중 오류가 발생한 파일은 결과 목록에서 제외하고 계속 진행
					continue;
				}
			}
		}

		return resultList; // 저장에 성공한 파일들의 저장명/원본명 목록
	}
}
