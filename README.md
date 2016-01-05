# README #

## 개발 계획 ##

### 1. 수집데이터  ###
| Feature(covariate)  | Description         
| :------------       | :-----------      
| Arrival time        | notification이 notification bar에 도착한 시간이다.          
| Removal time        | notification이 notification bar에서 삭제된 시간이다.          
| **Responsed Device**    | 응답에 사용된 장치 (Smartphone or Watch)
|Notification reponse| notification을 클릭했는지 안했는지에 대한 Boolean 정보          
| Response time           | 삭제시간 - 도착시간            
| Proximity           | 1분전에 사용자가 스마트폰 근처에 있었는지 아닌지 (boolean)            
| **co-location**           | RSSI를 이용해서 스마트폰과 Watch의 거리를 측정            
| recent phone usage           | 1분전에 phone을 사용했는지 안했는지 (boolean)     
| Sender Application         | notification을 발생 시킨 app 이름과 pacakge 이름
| **action buttion** | Watch에서 누를수 있는 action buttion을 notification이 포함하고 있는가?                 
| Physical activity|  현재 사용자의 활동 상태
| Location|  현재 사용자의 위치

### 2. 개발 환경 ###
필요한 라이브러리
- Android's Notification Listener Service (Notification 수집)
- Google's Activity Recognition API (Activity 수집)
- ESSensorManager (data 수집)

### 단계별 진행 ###
- Notification 후킹 
- Notification 기록 보관
- Notification Click 유무 판별
- 센싱 데이터와 함께 수집
- 생성된 모델 기반으로 Notification 제어 수행

## 실험 계획  ##

### Preliminary Study ###
실제 모바일 응용프로그램 6개를 대상으로 전력 소비를 측정 한다.
Notification 릴레이에 따른 전력소비 부하를 계산한다.

### 연구 결과  ###
수집된 데이터를 이용해서 모델을 생성함.
모델에 대해서 10-fold cross validation을 수행 한다.

실제 설치해서 줄어드는 에너지 소모에 대해서 산술적으로 계산 한다.
잘못된 prediction으로 발생하는 missing notification 부작용에 대해서 조사한다.

모델 생성을 위해서 발생하는 데이터 소집 부하를 측정 한다.
bactch learning과 online learning에 따른 차이와
몇일 이상이 지나면 더이상 data 수집을 할 필요 없음을 이야기 한다.