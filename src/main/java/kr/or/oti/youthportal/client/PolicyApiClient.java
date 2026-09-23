package kr.or.oti.youthportal.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import kr.or.oti.youthportal.dto.api.YouthPolicyApiItemDTO;
import kr.or.oti.youthportal.dto.api.YouthPolicyApiResponseDTO;
import kr.or.oti.youthportal.dto.api.YouthPolicyApiResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PolicyApiClient {

	private static final int PAGE_SIZE = 100;     // 한 번의 호출로 요청할 건수
	private static final int MAX_PAGE = 50;       // 안전장치: 무한루프 방지용 최대 페이지 수 (50 * 100 = 5000건, 현재 총량 2821건보다 넉넉함)
	private static final int MAX_RETRY = 3;        // 한 페이지당 최대 재시도 횟수
	private static final long RETRY_DELAY_MS = 1000; // 재시도 전 대기 시간
	private static final long PAGE_DELAY_MS = 300;   // 페이지 사이 텀 (너무 빠르게 연달아 호출하면 서버가 500을 내려줌)

	private final WebClient.Builder webClientBuilder; // HTTP 호출 도구 (스프링이 자동 구성해서 주입해줌)

	@Value("${youthportal.api.url}")
	private String apiUrl;

	@Value("${youthportal.api.key}")
	private String apiKey;

	// 온통청년 API를 호출해서 전체 정책 목록(외부 원본 형태 그대로)을 가져옴
	public List<YouthPolicyApiItemDTO> fetchPolicies() {
		List<YouthPolicyApiItemDTO> allItems = new ArrayList<>(); // 모든 페이지에서 모은 결과

		for (int pageNum = 1; pageNum <= MAX_PAGE; pageNum++) {
			List<YouthPolicyApiItemDTO> pageItems = fetchOnePageWithRetry(pageNum); // 한 페이지 호출 (실패 시 재시도)

			if (pageItems.isEmpty()) { // 더 가져올 게 없으면(또는 재시도까지 다 실패했으면)
				break; // 반복 종료
			}

			allItems.addAll(pageItems);

			if (pageItems.size() < PAGE_SIZE) { // 요청한 개수보다 적게 왔다면 마지막 페이지라는 뜻
				break; // 반복 종료
			}

			sleep(PAGE_DELAY_MS); // 다음 페이지 호출 전 잠깐 대기 (연속 호출로 인한 서버측 500 방지)
		}

		return allItems;
	}

	// 한 페이지를 호출하되, 실패하면 잠깐 대기 후 최대 MAX_RETRY번까지 재시도
	private List<YouthPolicyApiItemDTO> fetchOnePageWithRetry(int pageNum) {
		for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
			try {
				return fetchOnePage(pageNum);
			} catch (Exception e) { // 서버측 500 등 일시적인 오류로 간주하고 재시도
				log.warn("정책 API 호출 실패 (pageNum={}, {}번째 시도): {}", pageNum, attempt, e.getMessage());
				if (attempt == MAX_RETRY) { // 마지막 시도까지 실패하면 그 페이지는 포기
					log.error("정책 API 호출 {}회 모두 실패하여 pageNum={}부터는 가져오지 못했습니다.", MAX_RETRY, pageNum);
					return List.of();
				}
				sleep(RETRY_DELAY_MS);
			}
		}
		return List.of(); // 도달하지 않음 (컴파일러를 위한 안전망)
	}

	// 지정한 페이지 번호 하나를 호출해서 그 페이지의 정책 목록만 반환
	private List<YouthPolicyApiItemDTO> fetchOnePage(int pageNum) {
		YouthPolicyApiResponseDTO response = webClientBuilder
				// WebClient 기본 버퍼 한도(256KB)로는 정책 100건 분량 응답을 다 못 담아서 예외가 남 - 10MB로 늘림
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
				.build()
				.get()
				.uri(apiUrl + "?apiKeyNm=" + apiKey + "&pageNum=" + pageNum + "&pageSize=" + PAGE_SIZE)
				.retrieve()
				.bodyToMono(YouthPolicyApiResponseDTO.class) // 응답 JSON을 YouthPolicyApiResponseDTO로 변환
				.block(); // 동기 호출 (나머지 앱이 전부 동기식이라 통일)

		YouthPolicyApiResultDTO result = (response != null) ? response.getResult() : null; // 봉투 한 겹 더 벗기기

		if (result == null || result.getYouthPolicyList() == null) { // 응답이 없거나 목록이 비어있으면
			return List.of(); // 빈 리스트로 반환 (호출부에서 null 체크 안 해도 되게)
		}

		return result.getYouthPolicyList();
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // 인터럽트 상태 복원 (관례적 처리)
		}
	}
}
