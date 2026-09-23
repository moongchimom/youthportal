package kr.or.oti.youthportal.dto; // DTO(데이터 전달 객체) 패키지

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter // 화면(뷰)에서 읽기만 하면 되므로 getter만 생성
@Builder // 내부적으로 of()에서 빌더를 사용해 생성
public class PageResponseDTO<E> { // 목록 조회 결과 + 페이징 정보를 함께 담는 제네릭 응답 DTO

	private int page; // 현재 페이지 번호
	private int size; // 한 페이지당 건수
	private int total; // 전체 데이터 건수
	private int start; // 화면에 보여줄 페이지 번호 블록의 시작 번호
	private int end; // 화면에 보여줄 페이지 번호 블록의 끝 번호
	private boolean prev; // 이전 페이지 블록 존재 여부
	private boolean next; // 다음 페이지 블록 존재 여부
	private List<E> dtoList; // 실제 목록 데이터

	// 검색조건(PageRequestDTO)과 전체 건수, 목록 데이터를 받아 페이징 정보를 계산해주는 정적 팩토리 메서드
	public static <E> PageResponseDTO<E> of(PageRequestDTO pageRequestDTO, int total, List<E> dtoList) {

		int page = pageRequestDTO.getPage(); // 요청받은 현재 페이지
		int size = pageRequestDTO.getSize(); // 요청받은 페이지당 건수

		int end = (int) (Math.ceil(page / 10.0)) * 10; // 현재 페이지가 속한 10단위 블록의 끝 번호 계산
		int start = end - 9; // 블록의 시작 번호는 끝 번호에서 9를 뺀 값

		int last = (int) (Math.ceil(total / (double) size)); // 전체 건수 기준 마지막 페이지 번호
		if (last < 1) { // 데이터가 하나도 없으면
			last = 1; // 마지막 페이지를 최소 1로 보정 (0건일 때도 1페이지 블록은 정상적으로 표시되도록)
		}

		end = Math.min(end, last); // 블록의 끝 번호가 마지막 페이지를 넘지 않도록 보정

		boolean prev = start > 1; // 시작 번호가 1보다 크면 이전 블록이 존재
		boolean next = total > end * size; // 끝 페이지 이후에도 데이터가 더 있으면 다음 블록이 존재

		return PageResponseDTO.<E>builder() // 계산된 값들로 응답 객체 생성
				.page(page) // 현재 페이지
				.size(size) // 페이지당 건수
				.total(total) // 전체 건수
				.start(start) // 블록 시작 번호
				.end(end) // 블록 끝 번호
				.prev(prev) // 이전 블록 여부
				.next(next) // 다음 블록 여부
				.dtoList(dtoList) // 실제 데이터 목록
				.build(); // 최종 객체 생성
	}
}
