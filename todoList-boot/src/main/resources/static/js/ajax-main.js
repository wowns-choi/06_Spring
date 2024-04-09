// 요소 얻어와서 변수에 저장
const todoHeader = document.querySelector('#todoHeader');

const totalCount = document.querySelector('#totalCount');
const completeCount = document.querySelector('#completeCount');
const reloadBtn = document.querySelector('#reloadBtn');

const todoTitle = document.querySelector('#todoTitle');
const todoContent = document.querySelector('#todoContent');
const addBtn = document.querySelector('#addBtn');

// 할 일 목록 조회 관련 요소
const tbody = document.querySelector("#tbody");

// 할 일 상세조회와 관련된 요소를 가져올 것
const popupLayer = document.querySelector("#popupLayer");  //모달창 전체
const popupTodoNo = document.querySelector("#popupTodoNo"); // todoNo
const popupTodoTitle = document.querySelector("#popupTodoTitle"); //제목
const popupComplete = document.querySelector("#popupComplete"); //완료여부
const popupRegDate = document.querySelector("#popupRegDate"); //날짜
const popupTodoContent = document.querySelector("#popupTodoContent"); // 내용
const popupClose = document.querySelector("#popupClose"); // x 버튼


//상세 조회 버튼
const deleteBtn = document.querySelector("#deleteBtn");
const updateView = document.querySelector("#updateView");
const changeComplete = document.querySelector("#changeComplete");

//수정 레이어 버튼
const updateLayer = document.querySelector('#updateLayer');
const updateTitle = document.querySelector('#updateTitle');
const updateContent = document.querySelector("#updateContent");
const updateBtn = document.querySelector("#updateBtn");
const updateCancel = document.querySelector("#updateCancel");

// 전체 Todo 개수 조회 및 출력하는 함수 정의
function getTotalCount(){
    // 비동기로 서버에서 전체 Todo 개수 조회하는
    // fetch() API 코드 작성
    // fetch : 가져오다. 어디서? 서버 와 DB에서.

    //GET 방식 요청
    fetch('/ajax/totalCount') // 비동기 요청 수행 -> Promise 객체(비동기작업이 완료된 후 무조건 결과는 반환해주겠다는 약속의 객체) 반환
    .then(response => {
		// response : 비동기 요청에 대한 응답이 담긴 객체
		console.log(response);
		//response.text() : 응답 데이터를 문자열/숫자 형태로 변환한 
		//									결과를 가지는 Promise 객체 반환
			return response.text();
    }) //두번째 then 의 매개변수 (result)
    	// 첫 번째 then 에서 반환된 promise객체의 PromiseResult 값
    .then(result => {
		// result 매개변수 == Controller 메서드에서 반환해준 진짜 값.
		console.log(result);
		totalCount.innerText = result;
	})
}

getTotalCount();

// completeCount 값 비동기 통신으로 얻어와서 화면에 출력하기
function getCompleteCount(){
	
	// fetch() : 비동기로 요청해서 결과 대이터를 가져오기
	
	// 첫번째 then 의 response :
	// - 응답 결과, 요청 주소, 응답 데이터 등이 담겨있음. 
	
	// response.text() : 응답 데이터를 text 형태로 변환

	// 두 번째 then 의 result
	// - 첫 번째 then 에서 text 로 변환된 응답 데이터

	fetch('/ajax/completeCount')
	.then(response =>{
			return response.text();
	})
	.then(result => {
		console.log(result);
		// 여기서 뭘 해줘야 하냐면, 완료된 개수에 포함시켜줘야 함. 
		completeCount.innerText = result;
		
	})
	
}
getCompleteCount();

reloadBtn.addEventListener("click", function(e){
	getTotalCount(); 
	getCompleteCount();
})

//--------------------------------------------------------


//const todoTitle = document.querySelector('#todoTitle');
//const todoContent = document.querySelector('#todoContent');
//const addBtn = document.querySelector('#addBtn');

// 할일 추가 버튼 클릭시 동작
addBtn.addEventListener('click', function(){
	// 비동기로 할 일 추가하는 fetch() 코드 작성
	// - 요청 주소 : "/ajax/add"
	// - 데이터 전달 방식(method) : "POST" 방식
	
	// 파라미터를 저장한 JS 객체
	// 즉, JS객체 형태로 파라미터를 저장해두자.
	const param = {
		// Key : Value
		"todoTitle" : todoTitle.value,
		"todoContent" : todoContent.value
	};
	// 근데 JS 객체 형태로는 자바와 통신할 수 없다. 
	// 그래서, JS 객체를 JSON 형태로 변환해서 자바쪽으로 가져갈거임.
	
	fetch("/ajax/add", {
		// key : value 형태로 작성
		method : "POST", // POST 방식 요청
		headers : {"Content-Type" : "application/json"}, // 요청 데이터의 형식을 JSON 으로 지정	}
		body : JSON.stringify(param),  //param 이라는 JS 객체를 JSON  형식의 string 으로 변환
		
	})
	.then(resp => {
		return resp.text();
	})
	.then(temp => {
		
		if(temp > 0){
			// 성공
			alert("추가 성공!!");
			todoTitle.value = "";
			todoContent.value = "";
			
			getTotalCount();
			
			// 할 일 목록 다시 조회
			selectTodoList();
			
		} else{
			//실패
			alert("추가 실패..");
		}
		
	});
	
			
})


//----------------------------------------------------------

// 비동기(ajax) 로 할 일 상세 조회하는 함수
const selectTodo = (url) => {
	//매개변수로 들어온 건, ajax로 해당 todo 를 조회해주는 역할을 하는 controller 가 매핑해주는 url 주소
	// 예를 들어, "/ajax/detail?todoNo=10"
	//update Layer 가 띄워져 있다면, 숨기기
	updateLayer.classList.add('popup-hidden');
	
	fetch(url)
	.then(resp => {
		return resp.json(); // 응답 데이터가 JSON 인 경우, 이를 자동으로 Object 형태로 반환하는 메서드
										// 그래서, 마치 resp.text(); 하고 JSON.parse 를 한번에 해준 느낌인 것.
	} )
	.then(todo => {
		console.log(todo);
			
			popupTodoNo.innerText = todo.todoNo; //자바스크립트 객체에 접근할 때,  " . " 쓰면 됨. 
			popupTodoTitle.innerText = todo.todoTitle;
			popupComplete.innerText = todo.complete;
			popupRegDate.innerText = todo.regDate;
			popupTodoContent.innerText = todo.todoContent;
			
			//popup Layer 보이게 하기
			popupLayer.classList.remove("popup-hidden");
			

			
	})
}

//popup Layer 의 x 버튼 (#popupClose) 클릭 시 닫기
popupClose.addEventListener('click', function(){
		popupLayer.classList.add("popup-hidden");
})			


//-------------------------------------------------------------------

// 비동기로 할 일 목록을 조회하는 함수
const selectTodoList = () => {
	
	fetch("/ajax/selectList")
	.then(response => {
		return response.text(); // List 자료구조를 text() 로 받아본다.  
	})
	.then(result =>{
		console.log(result);
		console.log(typeof result); // string 으로 나옴. JSON 형식의 문자열이라는거임. 
														// 즉, JS 객체가 아니라, string 이라고. 
														// 당연하지. text() 로 받았잖아.
		//이 List자료구조를 어떻게 tbody 에 쉽게 바인딩할 수 있을까?
		// JSON 형태의 문자열을 JS 객체타입으로 변환해줄 수 있는게 있음. 
		// JSON.parse(JSON데이터) 라고 해주면, JSON 형식의 문자열이 JS객체로 바뀜.
		
		//JSON.stringify(JS Object) : JS객체를 -> JSON 형식의 string 으로 변환
		
		const todoList = JSON.parse(result);
		console.log(todoList); // 현재 todoList 에는 JS객체형태로 변환됨.
		
		//JS 객체로 바꿀 경우 어떤 이점이 있을까?
		
		//-----------------------------------------
		
		//기존에 출력되어있던 할 일 목록을 모두 삭제
		tbody.innerHTML = "";
		
		
		// #tbody 에 tr/td 요소를 생성해서 내용 추가
		// todoList 라는 JS객체를 가지고 향상된 FOR문을 돌림
		for(let todo of todoList){
			
			// tr 태그 생성
			const tr = document.createElement("tr");
			const arr = ['todoNo', 'todoTitle', 'complete', 'regDate'];
			
			//-----------------------------안 for문 : tr에붙일 td들 생성하는 역할---------------------------------------
			for(let key of arr){
				const td = document.createElement("td");
								//제목인 경우
								if(key === 'todoTitle'){
										const a = document.createElement("a"); // a 태그 생성
										a.innerText = todo[key]; //제목을 a 태그 내용으로 대입.
										a.href = "/ajax/detail?todoNo=" + todo.todoNo;
										td.append(a);
										tr.append(td);
									
										// a 태그 클릭 시 기본 이벤트(페이지 이동) 막기
										a.addEventListener("click", function(e){
											e.preventDefault();
											
											// 할 일 상세 조회 비동기 요청
											// e.target.href : 클릭된 a태그의 href 속성 값
											selectTodo(e.target.href);	
										});
									
										continue;
								 }
				td.innerText = todo[key];
				tr.append(td);
			}
			//-----------------------------안 for문 끝---------------------------------------
			
			//tbody의 자식으로 tr(한 행 ) 추가
			tbody.append(tr);
			
			
			
		}
		
	});
};


console.log('deleteBtn==========' + deleteBtn);

//삭제 버튼 클릭 시 해당 todo 를 지우는 ajax 요청
deleteBtn.addEventListener('click', function(e){
	
	console.log('11');
	
	//취소 클릭 시 아무것도 안함
	if(!confirm('정말 삭제하시겠습니까?')){
		return;
	}
	
	const todoNo = popupTodoNo.innerText;
	//Delete 방식으로 HTTP요청하는 방법
		fetch("/ajax/delete", { 
			method : 'DELETE', // DELETE 방식 요청 -> 컨트롤러에서 @DeleteMapping 처리
			// 데이터 하나를 전달해도 application/json 작성
			headers : {"content-type" : "application/json"},
			body :  todoNo //todoNo 값을 body 에 담아서 전달.  
							//@RequestBody 로 꺼냄. 근데, @RequestBody 를 이용하여 Todo 라는 DTO 에 바디에 담겨있는 데이터가 저절로 바인딩되게 하려면, 
							//HTTP 요청메세지 바디가 JSON 형식의 문자열이어야 하므로, JSON.stringify() 를 써야 함. 
							// 현재 todoNo 은 그냥 데이터 하나만 전달할 때 사용할 수 있는 것이며, 
							// 그래서 HTTP 요청 바디에 (만약 todoNo 에 담긴 값이 123 이었다면) 123 이렇게 가는거야.
		})
			.then(response => {
				return response.text();
			})
			.then(result => {
				console.log(result);
				if(result > 0){
					alert('삭제되었습니다.');
					//상제 조회 창 닫아주기
					popupLayer.classList.add("popup-hidden");
					
					// 전체, 완료된 할 일 개수 다시 조회
					// + 할 일 목록 다시 조회
					getTotalCount();
					getCompleteCount();
					selectTodoList();
					
					
					 
				} else{
					alert('삭제 중 오류 발생');
				}
			})
					

	
})

//선생님이 한걸로 대체했다.
changeComplete.addEventListener('click', function(e){
	
	// 변경할 할 일 번호, 완료 여부 (Y <-> N)
	const todoNo = popupTodoNo.innerText;
	
	const complete = (popupComplete.innertText === 'Y' ? 'N' : 'Y');
	
	const obj = {
		"todoNo" : todoNo,
		"complete" : complete,
	};
	
	//비동기로 완료 여부 변경
	fetch("/ajax/changeComplete",{
		method : 'PUT', //PUT 은 UPDATE 할 때 쓰는 것. 
		headers : {"Content-Type" : "application/json"}, //값을 하나만 보내더라도 headers 써줘야 함. 
		body : JSON.stringify(obj),
	})
	.then(response => {
		return response.text(); //string 타입으로 
	})
	.then(
		result => {
			if(result>0){
				//성공했다. 
				// update 된 데이터를 다시 조회해서 화면에 출력
				// -> 서버 부하가 큼!!!
				
				// selectTodo(); 를 호출하지 않고,
				// 서버 부하를 줄이기 위해 상세 조회에서 Y/N만 바꾸기
				popupComplete.innerText = complete;
				
				// getCompleteCount();
				// 서버 부하를 줄이기 위해 완료된 Todo 개수 +- 1
				console.log(completeCount.innerText);
				
				const count = Number(completeCount.innerText);
				
				if(complete == 'Y'){
					completeCount.innerText = count + 1;
				} else{
					completeCount.innerText = count - 1;
				}
				
				//서버 부하 줄이기 가능!! 
				selectTodoList();
				
			} else{
				// 실패했다. 
				alert('완료 여부 변경 실패!!');
			}
		
	})
})

updateView.addEventListener('click', function(e){
	// 기존 팝업 레이어는 숨기고
	popupLayer.classList.add("popup-hidden");
	
	//수정 레이어 보이게
	updateLayer.classList.remove('popup-hidden');
	//팝업 레이어에 작성된 제목, 내용을 얻어와 세팅
	updateTitle.value = popupTodoTitle.innerText;
	
	//updateContent 는 textarea 인데, popupTodoContent 는 div 다. 
	// div 태그에서의 줄바꿈은 br 로 인식하는데, 
	// textarea 는 \n으로 인식한다.
	//그래서, html에서의 br 을 \n 으로 바꿔줘야함. 
	
	updateContent.value = popupTodoContent.innerHTML.replaceAll("<br>", "\n"); // 이렇게 하면 해당 내용의 <br> 이 \n으로 대체됨. 
	
	updateContent.value = popupTodoContent.innerText;
	
	// 수정 레이어 -> 수정 버튼에 data-todo-no 속성 추가
	updateBtn.setAttribute("data-todo-no", popupTodoNo.innerText);	
	
});

//---------------------------------------------------------------------------

// 수정 레이어에서 취소 버튼(#updateCancel) 이 클릭되었을 때
updateCancel.addEventListener('click', function(){
	updateLayer.classList.add('popup-hidden');
})

updateBtn.addEventListener("click", e=> {
	
	//서버로 전달해야되는 값을 객체로 묶어둠
	const obj ={
		"todoNo" : e.target.dataset.todoNo,
		"todoTitle" : updateTitle.value,
		"todoContent" : updateContent.value
	};
	
	
fetch('/ajax/update',  {
	method: 'PUT',
	headers : {"Content-Type" : "application/json"},
	body : JSON.stringify(obj),
})
.then( resp => resp.text())
.then(result => {
	
	if(result > 0){
		// 업데이트 잘 됬다. 
		alert("수정 성공!");
		updateLayer.classList.add('popup-hidden');
//		popupLayer.classList.remove('popup-hidden');

	//수정 레이어 숨기기
	updateLayer.classList.add("popup-hidden");
	
	//목록 다시 조회
	selectTodoList();
	
	popupTodoTitle.innerText = updateTitle.value;
	
	popupTodoContent.innerHTML
		= updateContent.value.replaceAll("\n", "<br>");
		
	popupLayer.classList.remove("popup-hidden");


// 수정 레이어 있는 남은 흔적 제거
updateTitle.value = "";
updateContent.value = "";
updateBtn.removeAttribute("data-todo-no");
				
	} else{
		// 업데이트 안됨.
		alert('수정 실패');
		
	}
})
})







selectTodoList();




    




