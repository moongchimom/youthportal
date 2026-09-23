package kr.or.oti.youthportal.dto; // DTO(데이터 전달 객체) 패키지

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // 목록 검색조건이므로 getter/setter 일괄 생성
@Builder // 빌더 패턴 사용 가능
@AllArgsConstructor // 전체 필드 생성자 생성
@NoArgsConstructor // 기본 생성자 생성 (스프링이 쿼리파라미터를 바인딩할 때 필요)
public class PageRequestDTO { // 목록 조회 시 공통으로 쓰이는 페이징+검색 조건 DTO

	@Builder.Default // 빌더로 만들 때 기본값을 유지하도록 지정
	@Min(value = 1) // 최소 1페이지
	@Positive // 양수만 허용
	private int page = 1; // 현재 페이지 번호 (기본값 1)

	@Builder.Default // 빌더 기본값 유지
	@Min(value = 1) // 최소 1건
	@Max(value = 10) // 한 페이지 최대 10건까지만 허용
	@Positive // 양수만 허용
	private int size = 10; // 한 페이지에 보여줄 건수 (기본값 10)

	private String[] types; // 검색 대상 필드들 (예: "title","content" 체크박스 값) - 폼의 name="types"와 이름이 같아야 함

	private String keyword; // 검색어

	private String region; // 지역구 검색어 (정책 목록의 지역별 검색용)

	private boolean finished; // 마감된 정책도 포함해서 볼지 여부 (기본 false=진행중만)

	private LocalDate from; // 검색 시작일 (등록일/신청일 범위 검색용)

	private LocalDate to; // 검색 종료일

	private String link; // 페이징/검색 상태를 유지하기 위한 쿼리스트링 (매번 새로 계산)

	// 조회 시 건너뛸 행 수 계산 (OFFSET 절에 사용)
	public int getSkip() {
		return (page - 1) * size; // (현재페이지-1) * 페이지당건수 만큼 건너뜀
	}

	// 페이지 이동/검색폼 재제출 시 함께 붙일 쿼리스트링을 생성
	public String getLink() {
		StringBuilder sb = new StringBuilder(); // 쿼리스트링을 조립할 버퍼

		sb.append("size=").append(size); // 페이지당 건수 포함

		if (types != null) { // 검색 대상 타입이 지정되어 있다면
			for (String type : types) { // 각 타입마다 반복해서
				sb.append("&types=").append(type); // &types=xxx 형태로 추가 (체크박스 name="types"와 반드시 동일해야 재바인딩됨)
			}
		}

		if (keyword != null && !keyword.isBlank()) { // 검색어가 비어있지 않다면
			sb.append("&keyword=").append(URLEncoder.encode(keyword, StandardCharsets.UTF_8)); // URL 인코딩하여 추가
		}

		if (region != null && !region.isBlank()) { // 지역구 검색어가 비어있지 않다면
			sb.append("&region=").append(URLEncoder.encode(region, StandardCharsets.UTF_8)); // URL 인코딩하여 추가
		}

		sb.append("&finished=").append(finished); // 마감포함 여부 포함

		if (from != null) { // 시작일이 지정되어 있다면
			sb.append("&from=").append(from); // ISO 형식(yyyy-MM-dd)으로 추가
		}

		if (to != null) { // 종료일이 지정되어 있다면
			sb.append("&to=").append(to); // ISO 형식으로 추가
		}

		this.link = sb.toString(); // 계산된 결과를 필드에도 저장해둠

		return this.link; // 조립된 쿼리스트링 반환
	}

	// 특정 검색 타입이 현재 선택되어 있는지 여부 (체크박스 checked 속성에 사용)
	public boolean isCheckType(String type) {
		return types != null && Arrays.asList(types).contains(type); // 배열이 있고 해당 타입을 포함하면 true
	}
}
