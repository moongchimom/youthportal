package kr.or.oti.youthportal.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.oti.youthportal.dto.PageRequestDTO;
import kr.or.oti.youthportal.dto.PolicyDTO;

@Mapper
public interface PolicyDAO {

	List<PolicyDTO> selectList(PageRequestDTO pageRequestDTO); // 검색조건/페이징에 맞는 정책 목록 조회

	int selectTotalCount(PageRequestDTO pageRequestDTO); // 검색조건에 맞는 전체 정책 수 조회

	PolicyDTO selectOne(Long pno); // 정책 번호로 정책 상세 조회

	PolicyDTO selectBySourceId(String sourceId); // 출처ID(외부 API의 plcyNo)로 조회 - 중복 가져오기 방지용

	List<String> selectDistinctRegions(); // 검색폼 라디오 버튼용 - 현재 저장된 정책들의 지역구명 목록(중복제거)

	int insertPolicy(PolicyDTO policy); // 관리자 - 정책 신규 등록

	int updatePolicy(PolicyDTO policy); // 관리자 - 정책 수정

	int deletePolicy(Long pno); // 관리자 - 정책 삭제

	
}
