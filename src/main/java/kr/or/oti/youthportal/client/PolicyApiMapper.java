package kr.or.oti.youthportal.client;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import kr.or.oti.youthportal.dto.PolicyDTO;
import kr.or.oti.youthportal.dto.api.YouthPolicyApiItemDTO;

@Component
public class PolicyApiMapper {

	// 확인된 실제 날짜 포맷 (bizPrdBgngYmd/bizPrdEndYmd): yyyyMMdd, "연중"인 정책은 공백으로 옴
	private static final DateTimeFormatter API_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

	// 온통청년 자체 지역필터가 "중앙부처"를 구분할 때 쓰는 기관그룹코드 (실제 API 응답으로 확인함)
	private static final String CENTRAL_GOV_GROUP_CD = "0054001";

	// 온통청년 지역필터 체크박스(15개 시/도)와 동일한 목록. sprvsnInstCdNm이 이 접두어로 시작하면 그 뒤의 시/군/구까지만 취해서 담당 부서명은 버림.
	// 전라남도/광주광역시/전남광주특별시/전남광주통합특별시는 온통청년 자체가 "전남광주" 하나로 묶어서 관리하는 걸 그대로 따름.
	private static final String[][] REGION_PREFIXES = {
			{"서울특별시", "서울"},
			{"부산광역시", "부산"},
			{"대구광역시", "대구"},
			{"인천광역시", "인천"},
			{"대전광역시", "대전"},
			{"울산광역시", "울산"},
			{"세종특별자치시", "세종"},
			{"경기도", "경기"},
			{"충청북도", "충북"},
			{"충청남도", "충남"},
			{"전북특별자치도", "전북"},
			{"경상북도", "경북"},
			{"경상남도", "경남"},
			{"강원특별자치도", "강원"},
			{"제주특별자치도", "제주"},
			{"전남광주통합특별시", "전남광주"},
			{"전남광주특별시", "전남광주"},
			{"광주광역시", "전남광주"},
			{"전라남도", "전남광주"},
	};

	// sprvsnInstCdNm으로 못 찾은 경우("기타") 제목에서라도 지역 키워드를 찾아 재분류할 때 쓰는 목록.
	// 정식 명칭(충청남도 등)도 같이 넣음 - "충남"은 "충청남도"라는 글자 안에 연속으로 안 들어있어서(중간에 "청"이 낌)
	// 축약형만으로는 "충청남도 청년센터 운영" 같은 제목을 못 잡기 때문.
	// "경기"(운동경기와 혼동), "광주"(경기도 광주시와 혼동) 두 개는 다른 뜻으로 오탐될 여지가 있어 결과를 지켜봐야 함.
	private static final String[][] TITLE_REGION_KEYWORDS = {
			{"서울", "서울"},
			{"부산", "부산"},
			{"대구", "대구"},
			{"인천", "인천"},
			{"대전", "대전"},
			{"울산", "울산"},
			{"세종", "세종"},
			{"경기", "경기"},
			{"충청북도", "충북"}, {"충북", "충북"},
			{"충청남도", "충남"}, {"충남", "충남"},
			{"전라북도", "전북"}, {"전북", "전북"},
			{"경상북도", "경북"}, {"경북", "경북"},
			{"경상남도", "경남"}, {"경남", "경남"},
			{"강원도", "강원"}, {"강원", "강원"},
			{"제주", "제주"},
			{"전남광주", "전남광주"},
			{"광주", "전남광주"},
			{"전라남도", "전남광주"}, {"전남", "전남광주"},
	};

	// sprvsnInstCdNm이 도 이름 없이 "서산시"처럼 시/군 이름만 오는 경우 대비.
	// 전국에 이름이 하나뿐인 시/군만 등록함 - "고성군"은 강원/경남에 둘 다 있어 의도적으로 제외
	// (잘못된 지역으로 채우느니 "기타"로 남기는 게 안전함).
	private static final Map<String, String> COUNTY_TO_REGION = Map.ofEntries(
			Map.entry("가평군", "경기"), Map.entry("강릉시", "강원"), Map.entry("광명시", "경기"),
			Map.entry("괴산군", "충북"), Map.entry("군산시", "전북"), Map.entry("김해시", "경남"),
			Map.entry("동해시", "강원"), Map.entry("부천시", "경기"), Map.entry("서귀포시", "제주"),
			Map.entry("서산시", "충남"), Map.entry("속초시", "강원"), Map.entry("시흥시", "경기"),
			Map.entry("순창군", "전북"), Map.entry("영양군", "경북"), Map.entry("예천군", "경북"),
			Map.entry("용인시", "경기"), Map.entry("원주시", "강원"), Map.entry("음성군", "충북"),
			Map.entry("의성군", "경북"), Map.entry("의왕시", "경기"), Map.entry("익산시", "전북"),
			Map.entry("장수군", "전북"), Map.entry("제주시", "제주"), Map.entry("제천시", "충북"),
			Map.entry("창원시", "경남"), Map.entry("청주시", "충북"), Map.entry("충주시", "충북"),
			Map.entry("통영시", "경남"), Map.entry("화성시", "경기"),
			// 충청남도 시/군 (서산시 빼고 전부 누락돼 있었음)
			Map.entry("천안시", "충남"), Map.entry("공주시", "충남"), Map.entry("보령시", "충남"),
			Map.entry("아산시", "충남"), Map.entry("논산시", "충남"), Map.entry("계룡시", "충남"),
			Map.entry("당진시", "충남"), Map.entry("금산군", "충남"), Map.entry("부여군", "충남"),
			Map.entry("청양군", "충남"), Map.entry("홍성군", "충남"), Map.entry("예산군", "충남"),
			Map.entry("태안군", "충남"),
			// 그 외 누락돼 있던 시/군
			Map.entry("경산시", "경북"), Map.entry("춘천시", "강원"), Map.entry("사천시", "경남")
	);

	// sprvsnInstCdNm이 도 이름 없이 "양천구"처럼 자치구 이름만 오는 경우 대비.
	// 중구/동구/서구/남구/북구/강서구처럼 여러 광역시에 같은 이름이 반복되는 자치구는 제외하고,
	// 그 도시에만 있는 고유한 이름만 등록함.
	private static final Map<String, String> DISTRICT_TO_REGION = Map.of(
			"양천구", "서울",
			"금정구", "부산", "영도구", "부산", "사상구", "부산"
	);

	// 외부DTO(API 원본) -> 내부DTO(PolicyDTO) 변환. 필요한 필드만 골라 옮기는 역할.
	// plcySprtCn(지원내용)은 PolicyDTO의 supportContent에 해당하므로 의도적으로 매핑하지 않음.
	public PolicyDTO toPolicyDTO(YouthPolicyApiItemDTO item) {
		String[] applyPeriod = parseApplyPeriod(item.getAplyYmd()); // [0]=신청시작일, [1]=신청마감일 (yyyyMMdd 문자열)

		return PolicyDTO.builder()
				.sourceId(item.getPlcyNo()) // 외부 API의 plcyNo를 sourceId로 저장 (중복 방지용)
				.title(item.getPlcyNm())
				.content(item.getPlcyExplnCn())
				.category(buildCategory(item.getLclsfNm()))
				.region(buildRegion(item.getPvsnInstGroupCd(), item.getSprvsnInstCdNm(), item.getPlcyNm()))
				.targetAge(buildTargetAge(item.getSprtTrgtMinAge(), item.getSprtTrgtMaxAge()))
				.startDate(parseDate(item.getBizPrdBgngYmd()))
				.endDate(parseDate(item.getBizPrdEndYmd()))
				.applyStartDate(parseDate(applyPeriod[0]))
				.applyEndDate(parseDate(applyPeriod[1])) // 온통청년이 "마감" 여부를 판단하는 실제 기준 필드
				.build(); // pno/regDate는 등록 시 서버에서 채워지므로 여기서 세팅하지 않음
	}

	// "20260312 ~ 20260319" 형태의 신청기간 문자열을 시작일/종료일 yyyyMMdd 문자열 2개로 분리.
	// 공백이거나 형식이 예상과 다르면 상시 신청으로 간주해 둘 다 null 처리.
	private String[] parseApplyPeriod(String aplyYmd) {
		if (aplyYmd == null || aplyYmd.isBlank()) {
			return new String[] { null, null };
		}
		String[] parts = aplyYmd.split("~");
		if (parts.length != 2) {
			return new String[] { null, null };
		}
		return new String[] { parts[0].trim(), parts[1].trim() };
	}

	// CATEGORY는 DB에서 NOT NULL인데, API가 분류명을 안 주는 정책도 있어서 기본값으로 대체
	// API 원본 lclsfNm이 "일자리,일자리,일자리"처럼 같은 분류명을 중복해서 콤마로 이어붙여 주는 경우가 있어 중복 제거 후 재조합
	private String buildCategory(String lclsfNm) {
		if (lclsfNm == null || lclsfNm.isBlank()) {
			return "기타";
		}
		return Arrays.stream(lclsfNm.split(","))
				.map(String::trim)
				.distinct()
				.collect(Collectors.joining(","));
	}

	// 원본 sprvsnInstCdNm이 "경상남도 양산시 경제국 민생경제과"처럼 시/도+시/군/구+담당부서까지 다 붙어있어도
	// 시/도 17개 버킷(REGION_PREFIXES)으로만 묶고 시/군/구·부서명은 전부 버림 (드롭다운이 "강원", "강원 고성"처럼 쪼개지지 않게).
	// 중앙부처(전국 대상)는 부서명만 오는 경우가 많아 코드로 따로 판별.
	// sprvsnInstCdNm으로도 못 찾으면("재단법인경기도미래세대재단"처럼 접두어가 아니라 중간에 낀 경우 등) 제목에서 한 번 더 시도.
	private String buildRegion(String pvsnInstGroupCd, String sprvsnInstCdNm, String plcyNm) {
		if (CENTRAL_GOV_GROUP_CD.equals(pvsnInstGroupCd)) {
			return "중앙부처";
		}

		if (sprvsnInstCdNm != null) {
			for (String[] prefix : REGION_PREFIXES) {
				if (sprvsnInstCdNm.startsWith(prefix[0])) {
					return prefix[1]; // 시/군/구·부서명 상관없이 시/도 버킷 하나로 통일
				}
			}

			String firstToken = sprvsnInstCdNm.trim().split("\\s+")[0];
			String byCounty = COUNTY_TO_REGION.get(firstToken); // 도 이름 없이 "서산시"처럼 시/군 이름만 온 경우
			if (byCounty != null) {
				return byCounty;
			}
			String byDistrict = DISTRICT_TO_REGION.get(firstToken); // 도 이름 없이 "양천구"처럼 자치구 이름만 온 경우
			if (byDistrict != null) {
				return byDistrict;
			}
		}

		if (plcyNm != null) {
			// "서산시 자립준비청년 운전면허 취득지원 사업"처럼 sprvsnInstCdNm엔 부서명("가족지원과")만 오고
			// 시/군/구 이름은 제목에만 있는 경우 대비 - 같은 매핑표를 제목에도 적용.
			// 제목엔 "천안시"가 아니라 "천안"처럼 시/군/구 글자를 뗀 형태로 쓰는 경우가 많아 끝 글자를 뗀 형태로 검사함.
			String byCountyInTitle = matchStrippedSuffix(plcyNm, COUNTY_TO_REGION);
			if (byCountyInTitle != null) {
				return byCountyInTitle;
			}
			String byDistrictInTitle = matchStrippedSuffix(plcyNm, DISTRICT_TO_REGION);
			if (byDistrictInTitle != null) {
				return byDistrictInTitle;
			}

			for (String[] keyword : TITLE_REGION_KEYWORDS) {
				if (plcyNm.contains(keyword[0])) {
					return keyword[1]; // 제목 어디에든 지역 키워드가 있으면 그 지역으로 재분류
				}
			}
		}

		return "기타"; // 기관명/제목 어디에서도 지역을 특정할 수 없는 경우 (대학/재단/공사 등)
	}

	// 시/군/구 매핑표의 각 키에서 마지막 글자(시/군/구)를 뗀 형태("천안시"->"천안")로 제목에 포함되는지 검사
	private String matchStrippedSuffix(String plcyNm, Map<String, String> suffixMap) {
		for (Map.Entry<String, String> entry : suffixMap.entrySet()) {
			String bareForm = entry.getKey().substring(0, entry.getKey().length() - 1);
			if (plcyNm.contains(bareForm)) {
				return entry.getValue();
			}
		}
		return null;
	}

	// 최소/최대 연령 두 필드를 하나의 문자열로 합침 (예: "19~39세")
	private String buildTargetAge(String minAge, String maxAge) {
		if ((minAge == null || minAge.isBlank()) && (maxAge == null || maxAge.isBlank())) {
			return "전연령"; // 연령 제한 정보가 아예 없으면 나이 제한이 없는 것으로 간주
		}
		if ("0".equals(minAge) && "0".equals(maxAge)) { // API가 연령 제한 없는 정책을 0~0으로 주는 경우
			return "전연령";
		}
		return minAge + "~" + maxAge + "세";
	}

	// 날짜 문자열을 LocalDate로 변환 (값이 없거나 공백("연중" 등)이면 null 유지)
	private LocalDate parseDate(String dateStr) {
		if (dateStr == null || dateStr.isBlank()) {
			return null;
		}
		return LocalDate.parse(dateStr, API_DATE_FORMAT);
	}
}
