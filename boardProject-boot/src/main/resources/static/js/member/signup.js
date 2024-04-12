console.log('signup.js');

// **** 회원 가입 유효성 검사 ****

// 필수 입력 항목(빨강 별표시 있는 항목들. 즉, 주소 빼고.)의 유효성 검사 여부를 체크하기 위한 객체
// - true == 해당 항목은 유효한 형식으로 작성됨
// - false == 해당 항목은 유효하지 않은 형식으로 작성됨
const checkObj = {
    "memberEmail" : false,
    "memberPw" : false,
    "memberPwConfirm" : false,
    "memberNickname" : false,
    "memberTel" : false,
    "authKey" : false
};



//-----------------------------------------------------------------

/* 이메일 유효성 검사 */

// 1) 이메일 유효성 검사에 사용될 요소 얻어오기
const memberEmail = document.querySelector('#memberEmail');
const emailMessage = document.querySelector('#emailMessage');


// 2) 이메일이 입력(input) 될 때마다 유효성 검사 수행
memberEmail.addEventListener('input', function(e){

    // 이메일 인증 후 사용자가 이메일을 변경한 경우
    // 나중에 처리
    checkObj.authKey = false; // 일단 입력한 순간, 무조건 authKey 를 false로 해줌으로써, 이메일이 유효성검사를 통과했다는 증거를 삭제함.
    document.querySelector("#authKeyMessage").innerText = ""; 



    // 작성된 이메일의 값을 얻어오기
    const inputEmail = e.target.value;
    
    // 3) 입력된 이메일이 없을 경우 ( 사용자가 썻다 지웠을 경우 다시 '메일을 받을 수 있는 이메일을 입력해주세요' 를 입력해주기 위함임.)
    if(inputEmail.trim().length === 0){
        emailMessage.innerText = '메일을 받을 수 있는 이메일을 입력해주세요.';

        // 메세지에 색상을 추가하는 클래스 모두 제거
        emailMessage.classList.remove('confirm', 'error'); // , 로 클래스 여러개 쓸 수 있네

        // 이메일 유효성 검사 여부를 false 로 변경
        checkObj.memberEmail = false;

        // 잘못 입력한 띄어쓰기가 있을 경우 없앰. <- 이건 고려하지 못했었는데 이렇게 하는거구나
        memberEmail.value = "";

        return;

    }

    // 4) 입력된 이메일이 있을 경우 정규식 검사
    const regExp = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/; // 깨알 상식: naver.com 여기서 .com 같은 것들 중에 한글자인 경우는 없다. 

    // 입력 받은 이메일이 정규식과 일치하지 않는 경우
    if( !regExp.test(inputEmail) ){
        emailMessage.innerText = '알맞은 이메일 형식으로 작성해주세요.';
        emailMessage.classList.add('error'); // 글자를 빨강색으로 하는 css 추가
        emailMessage.classList.remove('confirm'); // 글자를 초록색으로 하는 css 제거. 왜? 알맞은 이메일형식을 썻다가 다시 수정했는데, 이메일형식이 틀린경우를 고려함.
        checkObj.memberEmail = false; // 유효하지 않은 이메일임을 기록.
        return;
    }

    // 5) 유효한 이메일 형식인 경우 
    // 5-1) 중복 검사 수행
    // 비동기(ajax) 로.
    fetch('/member/checkEmail?memberEmail=' + inputEmail)
    .then( resp => {return resp.text();})
    .then(count => {
        //count : 1 이면 중복. 0 이면 중복이 아니다. 
        if(count > 0){
            emailMessage.innerText = '이미 사용중인 이메일 입니다.';
            emailMessage.classList.add('error');
            emailMessage.classList.remove('confirm');
            checkObj.memberEmail = false; // 중복은 유효하지 않으므로.
            return;
        } 

        // 중복 X 경우
        emailMessage.innerText = '사용 가능한 이메일 입니다.';
        emailMessage.classList.add('confirm');
        emailMessage.classList.remove('error');
        checkObj.memberEmail = true; // 유효한 이메일

    })
    .catch(error => {
        // fetch() 수행 중 예외 발생 시 처리 
        console.log(error); // 발생한 예외 출력
    });




})

//---------------------------인증번호 관련--------------------------------

// 인증번호 받기 버튼
const sendAuthKeyBtn = document.querySelector('#sendAuthKeyBtn');

// 인증번호 입력 input
const authKey = document.querySelector("#authKey");

// 인증번호 입력 후 확인 버튼
const checkAuthKeyBtn = document.querySelector('#checkAuthKeyBtn');

// 인증번호 관련 메시지 출력 span
const authKeyMessage = document.querySelector("#authKeyMessage");

let authTimer; // 타이머 역할을 할 setInterval 을 저장할 변수

const initMin = 4; // 타이머 초기값 (분)
const initSec = 59; // 타이머 초기값 (초)
const initTime = "05:00";

// 실제 줄어드는 시간을 저장할 변수
let min = initMin; // min = 4
let sec = initSec; // sec = 59

// 인증번호 받기 버튼 클릭 시 
sendAuthKeyBtn.addEventListener('click', function(e){

     checkObj.authKey = false; 
    // 위 코드는 이전에 인증번호 받기 버튼을 눌러서, 인증을 받아서 checkObj.authKey 가 true 가 된 사용자가
    // 인증번호 받기 버튼을 다시 눌렀다면? 다시 인증해야 하는 사용자로 간주하고, checkObj.authKey 를 false로 바꿔줌

    authKeyMessage.innerText = ''; // 이 태그에 이메일 본인인증에 대한 메세지를 담아줬을 텐데, 만약 있을 경우 이를 지우겠다는 소리.

    //중복되지 않은 유효한 이메일을 입력한 경우가 아니라면
    if(!checkObj.memberEmail){
        alert("유효한 이메일 작성 후 클릭해주세요");
        return
    }

    // 클릭시 타임아웃 숫자 초기화
    min = initMin;
    sec = initSec;

    // 이전 동작중인 인터벌 클리어
    clearInterval(authTimer);

    // **********************************************
    // 비동기로 서버에서 메일보내기
    fetch("/email/signup", {
        method : "POST",
        headers : {"Content-Type" : "application/json"},
        body : memberEmail.value
    })
    .then(resp => {

        return resp.text();
    })
    .then(result => {

        if(result == 1){
            console.log("인증 번호 발송 성공");
        }else{
            console.log("인증 번호 발송 실패");
        }
    })
    .catch(error => {
        console.log(error);
    })


    //******************************************** */

    // 메일은 비동기로 서버에서 보내라고 하고, 
    // 타이머는 지금 자바스크립트로 타이머 시작하기

    authKeyMessage.innerText = initTime; // 05:00 세팅
    authKeyMessage.classList.remove("confirm", "error");

    alert("인증번호가 발송되었습니다.");

    // setInterval(함수, 지연시간(ms단위))   
    // - 지연시간(ms) 만큼 시간이 지날 때마다 함수 수행

    // clearInterval(Interval 이 저장된 변수)
    // - 매개변수로 전달받은 interval을 멈춤

    // 인증 시간 출력(1초 마다 출력)
    authTimer = setInterval(() => {
        authKeyMessage.innerText = `${addZero(min)}:${addZero(sec)}`;

        // 0분 0초인 경우("00:00" 출력 후)
        if(min == 0 && sec == 0){
            checkObj.authKey = false; // 인증 못함
            clearInterval(authTimer);
            authKeyMessage.classList.add('error');
            authKeyMessage.classList.remove('confirm');
            return;
        }

        // 0분은 아니지만, 초가 0인 경우
        if(sec == 0){
            sec = 60;
            min--;
        }

        // 남은 경우는, 초를 1초씩 감소시키는 로직
        sec--; // 1초 감소

    }, 1000);


});



// 전달 받은 숫자가 10 미만인 경우(한자리) 앞에 0 붙여서 반환해주는 함수
function addZero(number){
    if( number < 10 ) return "0" + number;
    else return number;
}


/* ----------------------------------------------------------------- */

// 인증하기 버튼 클릭 시 
// 입력된 인증번호를 비동기로 서버에 전달
// -> 입력된 인증번호와 발급된 인증번호가 같은지 비교
//      같으면 1, 아니면 0 반환
// 단, 타이머가 00:00초가 아닐 경우에만 수행

checkAuthKeyBtn.addEventListener('click', function(e){

    if(min === 0 && sec === 0){
        alert('시간초가 지났는데?');
        return;
    }

    //여기까지 왔다는 건, 시간초가 지나지 않은 상태에서 사용자가
    //인증하기 버튼을 눌렀다는 뜻. 

    if(authKey.value.length < 6){
        // 난수는 무조건 6자리인데, 6자리보다 작다는 건 
        // 얼토당토 않는 걸 사용자가 썻다는 거야. 
        // 왜 db 가서 검증하면 되는데, 굳이 여기 js 에서 이를 처리하는 걸까?
        // db 까지 접근할 필요가 없는 건 최대한 db까지 안가도록 하도록.
        alert('인증번호를 정확히 입력해주세요~');
        return;
    }

    //입력받은 이메일, 인증번호로 객체 생성
    const obj = {
        "email" : memberEmail.value,
        "authKey" : authKey.value
    };

    fetch("/email/checkAuthKey", {
        method : "POST",
        headers : {"Content-Type" : "application/json"},
        body : JSON.stringify(obj) //obj 를 JSON 형태의 문자열로 변환
    })
    .then(response => response.text())
    .then(result => {

        // == : 값 비교 --------"1" == 1 : true
        // === : 값 + 타입 비교 -------------- "1" === 1 : false 

        if(result == 0){
            alert("인증번호가 일치하지 않습니다!");
            checkObj.authKey = false;
            return;
        }
        // 여기까지 왔다는 건, 인증이 일치했다는 뜻.    
        // 타이머를 멈춰놔야 해. 
        clearInterval(authTimer); // 타이머 멈춤
        authKeyMessage.innerText = '인증 되었습니다.'; //문구 띄워주기
        authKeyMessage.classList.remove("error"); 
        authKeyMessage.classList.add("confirm");

        checkObj.authKey = true; // 유효성 검사 성공했다 라고 표시
        return;
        
    })
})


// ---------------------------------------------------------------

/* 비밀번호 / 비밀번호 확인 유효성 검사하기 : 이건 모두 js 로 해주면 됨. */

// 1) 비밀번호 관련 요소 얻어오기 
const memberPw = document.querySelector('#memberPw');
const memberPwConfirm = document.querySelector('#memberPwConfirm');
const pwMessage = document.querySelector('#pwMessage');

// 5) 비밀번호, 비밀번호확인이 같은지 검사하는 함수
const checkPw = () => {
    // 같을 경우
    if(memberPw.value === memberPwConfirm.value){
        pwMessage.innerText = "비밀번호가 일치합니다.";
        pwMessage.classList.add("confirm");
        pwMessage.classList.remove("error");
        checkObj.memberPwConfirm = true;
        return; 
    }

    // 같지 않을 경우
    pwMessage.innerText = "비밀번호가 일치하지 않습니다..";
    pwMessage.classList.add("error");
    pwMessage.classList.remove("confirm");
    checkObj.memberPwConfirm = false;

}

// 2) 비밀번호 유효성 감사
memberPw.addEventListener('input', function(e){
    const inputPw = e.target.value;

    // 입력되지 않은 경우 <- 비밀번호 썻다가 쓰여진 비밀번호를 모두 지웠을 때를 대비.
    if(inputPw.trim().length == 0){
        pwMessage.innerText = "영어,숫자,특수문자(!,@,#,-,_) 6~20글자 사이로 입력해주세요.";
        pwMessage.classList.remove("confirm", "error");
        checkObj.memberPw = false; // 비밀번호가 유효하지 않다고 표시
        memberPw.value = ""; // 처음에 띄어쓰기 입력 못하게 하기 
        return;
    }

    // 4) 입력받은 비밀번호 정규식 검사
    const regExp = /^[a-zA-Z0-9!@#_-]{6,20}$/;

    if(!regExp.test(inputPw)){
        // 유효성 검사 실패
        pwMessage.innerText="비밀번호가 유효하지 않습니다";
        pwMessage.classList.add("error");
        pwMessage.classList.remove("confirm");
        checkObj.memberPw = false;
        return;
    }
    // 유효한 경우
    pwMessage.innerText = '유효한 비밀번호 형식입니다';
    pwMessage.classList.add("confirm");
    pwMessage.classList.remove("error");
    checkObj.memberPw = true;

    //비밀번호 입력시 확인란의 값과 비교하는 코드 추가 



    // 비밀번호 확인에 값이 작성되어 있을 때에만 checkPw() 를 호출해서, 
    // 비밀번호와 비밀번호 확인이 일치하는지를 체크하겠다. 
    if(memberPwConfirm.value.length > 0){
        checkPw();
    }

    // 6) 비밀번호 확인 유효성 검사 
    // 단, 비밀번호(memberPw)가 유효할 때만 검사 수행
    memberPwConfirm.addEventListener("input", () => {
        if(checkObj.memberPw){
            checkPw();
            return;
        }

        // memberPw 가 유효하지 않은 경우 : 비밀번호가 유효하지 않은 경우
        // memberPwConfirm 검사 X : 비밀번호확인란을 유효성검사하지 않겠다. 
        checkObj.memberPwConfirm = false;


    })


})


// 닉네임 유효성 검사 

const memberNickname = document.querySelector("#memberNickname");
const nickMessage = document.querySelector("#nickMessage");

memberNickname.addEventListener("input", (e) => {

    const inputNickname = e.target.value;

    // 1) 입력 안한 경우
    if(inputNickname.trim().length ===0){
        nickMessage.innerText = "한글, 영어, 숫자로만 2~10글자";
        nickMessage.classList.remove("confirm", "error");
        checkObj.memberNickname = false;
        memberNickname.value = "";
        return;
    }

    // 2) 정규식 검사
    const regExp = /^[가-힣\w\d]{2,10}$/;

    if(!regExp.test(inputNickname)){
        //유효 X
        nickMessage.innerText = "유효하지 않은 닉네임 형식입니다.";
        nickMessage.classList.add("error");
        nickMessage.classList.remove("confirm");
        checkObj.memberNickname = false;
        return;
    }
    
    fetch("/member/checkNickname?memberNickname=" + inputNickname)
    .then(resp => resp.text())
    .then(count => {
        if(count == 1){
            // 중복 O
            nickMessage.innerText = "이미 사용중인 닉네임 입니다.";
            nickMessage.classList.add("error");
            nickMessage.classList.remove("confirm");
            checkObj.memberNickname = false;
            return;        
        }

        nickMessage.innerText = "사용 가능한 닉네임 입니다.";
        nickMessage.classList.add("confirm");
        nickMessage.classList.remove("error");
        checkObj.memberNickname = true;
    })
    .catch(err => console.log(err));


})




// 휴대폰 번호 유효성 검사

const memberTel = document.querySelector('#memberTel'); //전화번호 input창
const telMessage = document.querySelector('#telMessage'); // 전화번호 메세지 창

memberTel.addEventListener('input', function(e){

    if(memberTel.value.trim().length == 0){
        //아무것도 입력하지 않은 경우
        telMessage.innerText = '전화번호를 입력해주세요.(- 제외)';
        telMessage.classList.remove("confirm");
        telMessage.classList.remove("error");

        checkObj.memberTel = false; //이거 왜 썻냐면, 유효한 전화번호였다가 지웠을 수 있잖아. 

        telMessage.value = "";
    }

    // 입력한 경우
    // 1. 정규식 검사
    const regExp = /^01[0-9]{1}[0-9]{3,4}[0-9]{4}$/;

    if(!regExp.test(e.target.value)){
        //정규식을 통과하지 못한 경우
        telMessage.innerText = '전화번호 형식이 올바르지 않습니다. ';
        telMessage.classList.add("error");
        telMessage.classList.remove("confirm");
        checkObj.memberTel = false;
        return;
    }


    telMessage.innerText = '유효한 전화번호 형식입니다. ';
    telMessage.classList.add("confirm");
    telMessage.classList.remove("error");
    checkObj.memberTel = true;

    console.log(checkObj);
    return;

  
})




//------------------------------------------------------------------
// 회원 가입 버튼 클릭 시 전체 유효성 검사 여부 확인
const signupForm = document.querySelector("#signUpForm");

signupForm.addEventListener('submit', function(e){


    // checkObj 의 저장된 값(value) 중
    // 하나라도 false 가 있으면 제출하면 안됨. 


    // for ~ in (객체 전용 향상된 for 문)
    for (let key in checkObj){ // checkObj 요소의 key 값을 순서대로 꺼내옴
        if(!checkObj[key]){ // false 인 경우 
            let str; // 출력할 메세지를 저장할 변수

            switch(key){
                case "memberEmail" :
                    str = "이메일이 유효하지 않습니다."; break;
                case "authKey" :
                    str = "이메일이 인증되지 않았습니다."; break;
                case "memberPw" :
                    str = "비밀번호가 유효하지 않습니다."; break;
                case "memberPwConfirm" :
                    str = "비밀번호가 일치하지 않습니다."; break;
                case "memberNickname" :
                    str = "닉네임이 유효하지 않습니다."; break;
                case "memberTel" :
                    str = "전화번호가 유효하지 않습니다."; break;
            }

            // str 을 어떻게 해줘야 할까? 
            alert(str);
            document.getElementById(key).focus();
            e.preventDefault(); // form 태그 기본 이벤트(제출) 막기
            return;
        }
        
    }


})


























