//console.log("main.js.loaded");

// 쿠키에서 key 가 일치하는 value 얻어오기 함수

// 쿠키는 "K=V; K=V; K=V..." 형식으로 저장되어 있다. 

const getCookie = (key) => {
    
    document.cookie = "test" + "=" + "입니다";
    
    const cookies = document.cookie; // 쿠키들이 꺼내져서 cookies 라는 변수에 담김. 

    //console.log(cookies); // saveId=user01@kh.or.kr; test=입니다
    // 왜 나머지 쿠키들은 안나올까? 글쎄

    // cookies 문자열을 배열 형태로 변환해줘야 함. 
    const cookieList = cookies.split("; "); //주의 ";" 이 아니라, "; " 이다. 스페이스 조심.

    //console.log(cookieList); //['saveId=user01@kh.or.kr', 'test=입니다'] 로 나옴.

    const cookieList2 = cookieList.map( el => { return el.split("=")} ) // 배열.map(함수) : 배열의 각 요소를 이용해 함수를 수행한 후 결과값으로 새로운 배열을 만들어 반환
    // 위 코드 수행 결과 여러 개의 배열이 만들어진다. 
    // [saveId, user01@kh.or.kr]
    //console.log('aaaaa');
    //console.log(cookieList2);

    /*
    0: (2) ['saveId', 'user01@kh.or.kr']
    1: (2) ['test', '입니다']
    */

    // 배열을 객체로 변환 (그래야 다루기 쉽다.)
    const obj = {}; //비어있는 객체 선언
    for(let i=0; i<cookieList2.length; i++){
        const k = cookieList2[i][0]; // 배열에서 key 값 꺼내오기
        const v = cookieList2[i][1]; // 배열에서 value 값 꺼내오기
        obj[k] = v; //객체에 추가
    }

    console.log(obj);
    /*
    {s: 'a', t: 'e'}
    s: "a"
    t: "e"
    
    */

    /*
        왜 배열을 자바 객체로 변환했을까?
        배열은 인덱스 번호를 알고, 그 인덱스 번호의 키가 무엇인지 찾아야 해당 쿠키가
    */

    return obj[key]; // 매개변수로 전달받은 key와
                    //  obj 객체에 저장된 키가 일치하는 요소의 value 값 반환

}

console.log(getCookie("saveId")); //만약 saveId 라는 쿠키가 없다면, undefined 가 나옴.

const loginEmail = 
document.querySelector("#loginForm input[name='memberEmail']"); // 아이디 역할을 해주는 이메일 쓰는 그 input 태그를 선택함. 

if(loginEmail != null){ // 이 코드 써준 이유 : 로그인된 상태에서는 loginEmail 이라는 변수에 담은 태그가 존재하지 않기 때문에, 이 코드가 없는 경우 로그인된 화면에서는 자바스크립트에서 없는 걸 가지고 뭘 하려고 하느냐 라면서 에러를 발생시킴. 

    // 쿠키 중 key 값이 "saveId" 인 요소의 value 얻어오기
    const saveId = getCookie("saveId"); //undefined 또는 이메일

    //saveId 값이 있을 경우 == 아이디 저장 체크박스를 선택하고 로그인해서, saveId 라는 키의 쿠키를 저장한 경우
    if(saveId != undefined){
        loginEmail.value = saveId; // 쿠키에서 얻어온 값을 input 에 value 로 세팅

        //아이디 저장 체크박스에 체크 해두기
        document.querySelector("input[name='saveId']").checked = true;
    }
}


//이메일, 비밀번호 미작성 시 로그인 막기
const loginForm = document.querySelector("#loginForm");

//이메일은 위의 코드에서 loginEmail 이라는 변수에 저장해둠. 

const loginPw = document.querySelector("#loginForm input[name = 'memberPw']")


// 로그인 폼이 화면에 존재할 때. 즉, 로그인 전의 홈화면일 경우
if(loginForm != null){
    //제출 이벤트 발생 시 
    loginForm.addEventListener('submit', function(e){
        
        // 이메일 미작성
        if(loginEmail.value.trim().length === 0){
            alert('이메일을 작성해주세요');
            e.preventDefault();
            loginEmail.focus(); // 초점 이동
            return;
        }
        // 비밀번호 미작성 시
        if(loginPw.value.trim().length === 0){
            alert('비밀번호를 작성해주세요');
            e.preventDefault();
            loginPw.focus(); // 초점 이동
            return;
        }

    })

}
/*
const testLogin = document.querySelector('#test-login');


testLogin.addEventListener('click', function(e){
	
	fetch('/member/testLogin?memberEmail=' + testLogin.innerText)
	.then(resp => resp.text())
	.then(result => {
		
		if(result == 1){
			// 멤버를 찾은 경우
			window.location.href = '/';
		}
		else{
			alert('존재하지 않는 아이디입니다.');
		}
	})
	.catch(error => console.log((error)))
	
	
})

*/

/* 빠른 로그인 */
const quickLoginBtns = document.querySelectorAll(".quick-login");

quickLoginBtns.forEach((item, index) => {
    // item : 현재 반복 시 꺼내온 객체
    // index : 현재 반복 중인 인덱스

    // quickLoginBtns 요소인 button 태그 하나씩 꺼내서 이벤트 리스너 추가
    item.addEventListener("click", () => { // 각 버튼에 클릭 이벤트 추가
        const email = item.innerText; // 버튼에 작성된 이메일 얻어오기

        location.href = "/member/quickLogin?memberEmail=" + email;
    })
});

const selectMemberList = document.querySelector("#selectMemberList");

const tbody = document.querySelector("#tbody");

selectMemberList.addEventListener('click', function(e){
	
	fetch('/member/selectMemberList')
	.then(resp => resp.json())
	.then(result => {
		result.forEach((item, index) =>  {
			let tr = document.createElement("tr");
			
			let td1 = document.createElement("td");
			td1.innerText = item.memberNo;
			let td2 = document.createElement("td");
			td2.innerText = item.memberEmail;
			let td3 = document.createElement("td");
			td3.innerText = item.memberNickname;
			let td4 = document.createElement("td");
			td4.innerText = item.memberDelFl;
			
			tr.append(td1);
			tr.append(td2);
			tr.append(td3);
			tr.append(td4);
			tbody.append(tr);			
		})

		
	})
	.catch(err => console.log(err));
})

const memberNo1 = document.querySelector('#resetMemberNo'); //input 1
const resetPwBtn = document.querySelector('#resetPw'); //input 1

resetPwBtn.addEventListener('click', function(e){
	let inputMemberNo = memberNo1.value;
	fetch('/member/updatePwToPass01?inputMemberNo=' + inputMemberNo)
	.then(resp => resp.text())
	.then(result => {
		
		if(result == 1){
			alert('비밀번호 변경 잘됨');
		}else{
			alert('비밀번호 변경 중 오류발생');
		}
	})
})



//----------------------------------------------
const memberNo2 = document.querySelector('#restorationMemberNo'); //inpu2
const resetorationBtn = document.querySelector('#resetorationBtn'); //input 1

resetorationBtn.addEventListener('click', function(e){
	
	let memberNo2Value = memberNo2.value;
	
	// 클릭하면, fetch 로 탈퇴복구할 회원번호 전달
	fetch('/member/restore?updateMemberDelFl=' + memberNo2Value)
	.then(resp => resp.text())
	.then(result => {
		alert('aaaaaaaaaaaaaa' + result);
		
		if(result == 1){
			alert('회원 복구됨');
		} else{
			alert('복구중 예외 발생');
		}
		
		
		
		
	})	
})



