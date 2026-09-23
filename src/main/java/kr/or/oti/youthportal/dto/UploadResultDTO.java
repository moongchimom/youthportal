package kr.or.oti.youthportal.dto; // DTO(데이터 전달 객체) 패키지

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;

@Getter // 저장 결과는 읽기 전용으로만 쓰이므로 getter만 생성
public class UploadResultDTO { // 파일 1건을 실제로 서버에 저장하고 그 결과(저장명/원본명)를 담는 DTO

	private final String savedName; // 서버 저장 파일명 (UUID_원본명, 중복 방지)
	private final String originalName; // 업로드 당시 원본 파일명

	// 생성과 동시에 실제 파일 저장까지 수행 (업로드가 필요한 모든 곳에서 이 DTO 하나만 생성하면 저장까지 끝남)
	public UploadResultDTO(String uploadDir, MultipartFile file) throws IOException {
		this.originalName = StringUtils.cleanPath(file.getOriginalFilename()); // 경로 조작 문자 제거한 원본 파일명
		this.savedName = UUID.randomUUID() + "_" + this.originalName; // 중복 방지를 위해 UUID를 앞에 붙인 저장용 파일명

		Path uploadPath = Paths.get(uploadDir); // 업로드 디렉터리 경로
		if (!Files.exists(uploadPath)) { // 디렉터리가 아직 없다면
			Files.createDirectories(uploadPath); // 디렉터리 생성
		}

		Path targetPath = uploadPath.resolve(this.savedName); // 최종 저장 경로
		Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING); // 업로드된 파일 내용을 저장 경로로 복사
	}
}
