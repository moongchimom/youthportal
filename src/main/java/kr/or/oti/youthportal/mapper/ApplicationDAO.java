package kr.or.oti.youthportal.mapper; // MyBatis 매퍼(DAO) 인터페이스 패키지

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.oti.youthportal.dto.ApplicationDTO;
import kr.or.oti.youthportal.dto.PageRequestDTO;

@Mapper // 스프링이 이 인터페이스를 빈으로 등록하고 MyBatis 매퍼로 연결
public interface ApplicationDAO { // 신청 관련 SQL을 수행하는 매퍼 인터페이스 (중복신청 방지는 다이어그램 충실도를 위해 의도적으로 미포함)

	int insertApplication(ApplicationDTO application); // 신청 등록 (성공 시 application.appNo에 채번된 번호가 채워짐)

	ApplicationDTO selectById(Long appNo); // 신청 번호로 상세 조회 (정책명/회원명 포함)

	List<ApplicationDTO> selectByUserId(@Param("userId") String userId, @Param("pageRequestDTO") PageRequestDTO pageRequestDTO); // 특정 회원의 신청 목록 조회 (페이징)

	int selectCountByUserId(String userId); // 특정 회원의 신청 전체 건수 조회 (페이징 계산용)

	List<ApplicationDTO> selectAllApplications(PageRequestDTO pageRequestDTO); // 관리자용 전체 신청 목록 조회 (페이징+검색)

	int selectAllCount(PageRequestDTO pageRequestDTO); // 관리자용 전체 신청 건수 조회 (페이징 계산용)

	int updateStatus(@Param("appNo") Long appNo, @Param("status") String status); // 신청 상태 변경 (관리자 승인/반려, 본인 취소 공용)
}
