//목록으로 버튼 동작
const goToList = document.querySelector('#goToList');

goToList.addEventListener('click', function(e){
	location.href="/"; //메인 페이지 요청
});

// 완료 여부 변경 버튼 동작
const completeBtn = document.querySelector('.complete-btn');

completeBtn.addEventListener('click', function(e){
	const todoNo = e.target.dataset.todoNo;
	//console.log(todoNo);
	
	// Y <-> N 변경
	let complete = e.target.innerText; //기존 완료 여부 값 얻어오기
	console.log(complete);
	complete = (complete === 'Y') ? 'N' : 'Y'; 
	
	//완료 여부 수정 요청하기
	location.href = `/todo/changeComplete?todoNo=${todoNo}&complete=${complete}`;


})


const updateBtn = document.querySelector('#updateBtn');
updateBtn.addEventListener('click', function(e){
		const todoNo = e.target.dataset.todoNo;
		
		location.href = `/todo/update?todoNo=${todoNo}`;	
})

// 삭제 버튼 클릭 시
const deleteBtn = document.querySelector('#deleteBtn');

deleteBtn.addEventListener('click', function(e){
	if(confirm('삭제하시겠습니까?')){
		location.href = `/todo/delete?todoNo=${e.target.dataset.todoNo}`;	
	}
	
	
});
