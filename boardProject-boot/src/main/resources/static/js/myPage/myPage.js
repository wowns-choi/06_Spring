/* 회원 정보 수정 페이지 */
const updateInfo = document.querySelector('#updateInfo'); // 폼태그

//#updateInfo 요소가 존재할 때만 수행
if(updateInfo != null){
    // form 제출 시 
    updateInfo.addEventListener('submit', e => {
        const memberNickname = document.querySelector('#memberNickname');
        const memberTel = document.querySelector('#memberTel');

        //querySelectorAll : 배열로 들어옴.
        const memberAddress = document.querySelectorAll("[name='memberAddress']");

        //닉네임 유효성 검사
        if(memberNickname.value.trim().length == 0){ //이름란에 아무것도 안쓰고 폼제출할 경우
            alert("닉네임을 입력해주세요.");
            e.preventDefault(); //제출막기
            return; 
        }

        // 닉네임이 있다. 
        // 닉네임 정규식
        let regExp = /^[가-힣\w\d]{2,10}$/;
        if(!regExp.test(memberNickname.value)){
            //정규식에 걸려 실패했다면~
            alert("닉네임이 유효하지 않습니다.");
            e.preventDefault(); // 제출 막기
            return;
        } 

        // ************ 닉네임 중복검사는 개별적으로 하십쇼 ***********
        // 테스트 시 닉네임 중복 안되게 조심하기!
        // *********************************************************

        // 전화번호 유효성 검사
        if(memberTel.value.trim().length === 0){
            alert("전화번호를 입력해 주세요.");
            e.preventDefault();
            return;
        }

        // 전화번호 정규식에 맞지 않으면
        regExp = /^01[0-9]{1}[0-9]{3,4}[0-9]{4}$/;
        if(!regExp.test(memberTel.value)){
            alert('전화번호가 유효하지 않습니다.');
            e.preventDefault();
            return;
        }
        // 주소 유효성 검사
        // 입력을 안하면 전부 안해야되고
        // 입력하면 전부 해야된다. 

        const addr0 = memberAddress[0].value.trim().length == 0;
        const addr1 = memberAddress[1].value.trim().length == 0;
        const addr2 = memberAddress[2].value.trim().length == 0;

        // 모두 true 인 경우만 true 저장
        const result1 = addr0 && addr1 && addr2; // 아무것도 입력하지 않은 경우

        // 모두 false 인 경우만 true 저장
        const result2 = !(addr0 || addr1 || addr2); // 모두 다 입력한 경우다. 
        // 언어적으로 표현해보면, 하나라도 true인 경우 false 를 반환한다. 
        // 따라서, true 가 나오려면, 세개 모두 false 여야 함.  

        // 모두 입력 또는 모두 미입력이 아니면
        if(!(result1 || result2)){
            alert("주소를 모두 작성 또는 미작성 해주세요");
            e.preventDefault();
            return;
        }
    });
}

// --------------------------------------------------------------------
/* 비밀번호 수정 */

// 비밀번호 변경 form 태그
const changePw = document.querySelector('#changePw');
const myPageSubmit = document.querySelector('myPage-submit');

if(changePw != null){
    // 제출 되었을 때
    changePw.addEventListener("submit", function(e){

        const currentPw = document.querySelector('#currentPw');
        const newPw = document.querySelector('#newPw');
        const newPwConfirm = document.querySelector('#newPwConfirm');

        // - 값을 모두 입력했는가
        let str; // 변수 선언만 하면, undefined 상태임. 
        if(currentPw.value.trim().length == 0){
            str = "현재 비밀번호를 입력해주세요";
        } else if(newPw.value.trim().length == 0){
            str = "새 비밀번호를 입력해주세요";
        } else if(newPwConfirm.value.trim().length == 0){
            str = "새 비밀번호 확인을 입력해주세요";
        }

        if(str != undefined){
            // 현재 비밀번호, 새 비밀번호, 새 비밀번호 확인 중 하나를 안 썻을 경우
            alert(str);
            e.preventDefault();
            return;
        }

        // 새 비밀번호 정규식
        const regExp = /^[a-zA-Z0-9!@#_-]{6,20}$/;
        if(!regExp.test(newPw.value)){
            alert('새 비밀번호가 유효하지 않습니다.');
            e.preventDefault();
            return; 
        } 

        // 새 비밀번호 == 새 비밀번호 확인
        if(newPw.value != newPwConfirm.value){
            alert("새 비밀번호가 일치하지 않습니다.");
            e.preventDefault();
            return;
        }


    });
}

// -------------------------------------------------------
// 탈퇴 유효성 검사 

// 탈퇴 form 태그
const secession = document.querySelector("#secession");

if(secession != null){

    secession.addEventListener("submit", function(e){
        const memberPw = document.querySelector("#memberPw");
        const agree = document.querySelector("#agree");

        // - 비밀번호 입력 되었는지 확인해보자. 
        if(memberPw.value.trim().length == 0){
            alert("비밀번호를 입력해주세요");
            e.preventDefault(); // 제출막기
            return;
        }

        // 약관 동의 체크 확인
        // checkbox 또는 radio 는 checked 속성 을 이용할 수 있다. 
        // - checked -> 체크 시 true, 미체크시 false 반환 

        if(!agree.checked){
            // 체크되지 않았다면, 
            alert("약관에 동의해주세요");
            e.preventDefault();
            return;
        }
        
        // 정말 탈퇴? 물어보기
        if(!confirm("정말 탈퇴하시겠습니까?")){
            // 취소 눌렀을 때
            alert('취소 되었습니다.');
            e.preventDefault();
        } else{


        }



    })
}



