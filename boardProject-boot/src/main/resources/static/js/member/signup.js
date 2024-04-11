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
const authKey = document.querySelector("#authkey");

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
        
    })
    .then(result => {

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







