package kr.or.oti.youthportal.service; // 서비스(비즈니스 로직) 패키지

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import kr.or.oti.youthportal.client.PolicyApiClient;
import kr.or.oti.youthportal.client.PolicyApiMapper;
import kr.or.oti.youthportal.dto.PageRequestDTO;
import kr.or.oti.youthportal.dto.PageResponseDTO;
import kr.or.oti.youthportal.dto.PolicyDTO;
import kr.or.oti.youthportal.dto.api.YouthPolicyApiItemDTO;
import kr.or.oti.youthportal.mapper.PolicyDAO;
import lombok.RequiredArgsConstructor;


@Service // 스프링이 이 클래스를 서비스 빈으로 등록
@RequiredArgsConstructor // policyDAO 등 final 필드를 주입받는 생성자를 자동 생성

public class PolicyServiceImpl implements PolicyService{

	private final PolicyDAO policyDAO; // 정책 관련 SQL을 수행하는 매퍼
	private final PolicyApiClient policyApiClient; // 온통청년 API 호출 담당
	private final PolicyApiMapper policyApiMapper; // 외부DTO -> 내부DTO 변환 담당

	@Override
	public PageResponseDTO<PolicyDTO> getList(PageRequestDTO pageRequestDTO) {
		int total = policyDAO.selectTotalCount(pageRequestDTO); // 검색조건에 맞는 전체 건수 조회
		var policies = policyDAO.selectList(pageRequestDTO); // 검색조건+페이징에 맞는 목록 조회
		return PageResponseDTO.of(pageRequestDTO, total, policies); // 페이징 정보와 함께 응답 조립
	}

	@Override
	public PolicyDTO getPolicy(Long pno) {
		PolicyDTO policy = policyDAO.selectOne(pno); // 정책 번호로 상세 조회

		return policy;
	}

	@Override
	public List<String> getRegionList() {
		return policyDAO.selectDistinctRegions(); // 현재 저장된 정책들의 지역구명 목록(중복제거)
	}

	@Override
	public void registerPolicy(PolicyDTO dto) {
		dto.setRegDate(LocalDateTime.now()); // 등록일시는 클라이언트 값을 믿지 않고 서버 시간으로 고정
		policyDAO.insertPolicy(dto); // DB에 등록
	}

	@Override
	public void modifyPolicy(PolicyDTO dto) {
		policyDAO.updatePolicy(dto); // DB에 수정 반영 (UPDATE 문에 REG_DATE가 없어 등록일시는 그대로 유지됨)
	}

	@Override
	public void removePolicy(Long pno) {
		try {
			policyDAO.deletePolicy(pno); // 정책 삭제 시도
		} catch (DataIntegrityViolationException e) { // 해당 정책에 신청 내역이 있어 외래키 제약에 걸린 경우
			throw new IllegalStateException("해당 정책에 신청 내역이 있어 삭제할 수 없습니다."); // 사용자 친화적 메시지로 변환
		}
	}
	
	@Override
	public int importFromApi() {
		List<YouthPolicyApiItemDTO> items = policyApiClient.fetchPolicies(); // 외부 API에서 원본 목록 조회

		int count = 0; // 등록+수정 합쳐서 처리한 건수
		for (YouthPolicyApiItemDTO item : items) {
			PolicyDTO dto = policyApiMapper.toPolicyDTO(item); // 외부DTO -> 내부DTO 변환

			PolicyDTO existing = policyDAO.selectBySourceId(dto.getSourceId()); // 이미 가져온 정책인지 확인

			if (existing == null) {
				registerPolicy(dto); // 처음 보는 정책 -> 신규 등록
			} else {
				dto.setPno(existing.getPno()); // 기존에 저장된 pno를 알아야 그 행을 UPDATE할 수 있음
				modifyPolicy(dto); // 이미 있던 정책 -> 최신 내용으로 갱신
			}

			count++;
		}

		return count;
	}

}

