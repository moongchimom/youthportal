package kr.or.oti.youthportal.mapper; // 매퍼(DAO) 인터페이스 패키지 선언

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.oti.youthportal.domain.User;
import kr.or.oti.youthportal.dto.PageRequestDTO;
import kr.or.oti.youthportal.dto.UserDTO;

@Mapper // 스프링이 이 인터페이스의 구현체(프록시)를 자동 생성하도록 지정
public interface UserDAO { // USERS 테이블에 접근하는 매퍼 인터페이스

	UserDTO selectById(String userId); // 아이디로 회원 한명 조회 (로그인 검증용)

	int checkUserId(String userId); // 아이디 중복여부 확인 (존재하면 1 이상)

	int insertUser(User user); // 회원가입 - 신규 회원 저장

	List<User> selectMemberList(PageRequestDTO pageRequestDTO); // 관리자 회원목록 - 검색조건/페이징에 맞는 회원 목록 조회

	int selectMemberTotalCount(PageRequestDTO pageRequestDTO); // 관리자 회원목록 - 검색조건에 맞는 전체 회원 수 조회

	int updateMemberByAdmin(User user); // 관리자 회원수정 - 탈퇴/잠금/실패횟수 초기화 반영
}
