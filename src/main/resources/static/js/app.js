// 청년정책포탈 공통 자바스크립트

// 회원가입 폼: 비밀번호/비밀번호 확인 입력값이 다르면 제출을 막고 안내
document.addEventListener("DOMContentLoaded", function () {
	const joinForm = document.getElementById("joinForm"); // 회원가입 폼 요소 찾기

	if (joinForm) { // 회원가입 화면일 때만 동작
		joinForm.addEventListener("submit", function (event) {
			const upw = document.getElementById("upw").value; // 비밀번호 입력값
			const upwConfirm = document.getElementById("upwConfirm").value; // 비밀번호 확인 입력값

			if (upw !== upwConfirm) { // 두 값이 다르면
				event.preventDefault(); // 폼 제출을 막고
				alert("비밀번호가 일치하지 않습니다."); // 사용자에게 안내
			}
		});
	}
});
