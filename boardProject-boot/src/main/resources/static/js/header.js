// 공공 데이터 

// async & await : 비동기 처리 패턴 
// 비동기를 마치 동기처럼(실행 순서를 지켜서) 사용하는 방법 
// async: 비동기가 수행되는 함수 정의 부분 앞에 붙여 사용하는 키워드 (비동기 요청을 수행할 것이다)
// await: promise를 리턴하는 비동기 요청 앞에 붙여 사용하는 키워드 (응답이 올때까지 기다리겠다)
// promise 라는 건 fetch에서 리턴되는 게 promise객체임 

// 비동기 요청 1번째 함수 
// 서비스키 config.properties 에서 얻어오기 

//오늘 날짜를 YYYYMMDD 형식으로 리턴하는 함수 
function getCuurentDate(){
	const today = new Date();
	const year = today.getFullYear();
	const month = today.getMonth() + 1;
	const day = today.getDay();
	
	console.log(month);
	return '20210628';

}
// 비동기 요청 1번째 함수 
// 서비스키 config.properties에서 얻어오기 
async function getService (){
	
	try{
		// await 를 붙였으니, fetch의 결과인 res 를 response라는 변수에 담을 때까지 다음 요청을 하지 않는다. 
		const response = await fetch("/getServiceKey"); // fetch 의 결과가 response 에 담김 
		return response.text();
		
		
		
		/*
		fetch("/getServiceKey")
		.then(res => res.text())
		.then(result => {
			console.log(result);
		})
		*/
		
	}catch(err){
		console.log("getServiceKey의 에러 :" + err);
	}
	
}
// 공공데이터 날씨 api 정보를 얻어올 함수 
async function fetchData(){
	
	const currentDate = getCuurentDate(); // 20240524
	
	const serviceKey = await getService(); // 비동기 요청 1번째의 응답이 올때까지 기다림 
	console.log(serviceKey);
	
	const url = 'http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst';
	
	// URLSearchParams : URL 의 쿼리문자열을 쉽게 다룰 수 있게 해주는 내장 객체
	// 단 사용 시 decode 서비스키 사용 -> URLSearchParams 이 데이터를 인코딩하기 떄문
	const queryParams = new URLSearchParams({
		serviceKey : serviceKey,
		pageNo : 1,
		numOfRows : 10,
		dataType : 'JSON',
		base_date: currentDate,
		base_time : '0500',
		nx:60,
		ny:127 
	});
	
	console.log(`${url}?${queryParams}`);
	
	//fetch 요청 
	
	
}

fetchData();






