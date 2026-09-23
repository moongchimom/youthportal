package kr.or.oti.youthportal.mapper; // MyBatis 매퍼(DAO) 인터페이스 패키지

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.oti.youthportal.dto.ApplicationFileDTO;

@Mapper // 스프링이 이 인터페이스를 빈으로 등록하고 MyBatis 매퍼로 연결
public interface ApplicationFileDAO {

	int insertFile(ApplicationFileDTO applicationFile);

	List<ApplicationFileDTO> selectByAppNo(Long appNo); 

	ApplicationFileDTO selectOne(Long fileNo); 
}
