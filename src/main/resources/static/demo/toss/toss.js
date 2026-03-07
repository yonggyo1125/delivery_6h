const clientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
const customerKey = "USER_" + Math.random().toString(36).substring(2, 11);
const paymentWidget = PaymentWidget(clientKey, customerKey);

// 초기값 설정
let amount = parseInt(document.getElementById("order-amount-input").value);
document.getElementById("order-id-input").value = "49191805-a08f-463d-b6a2-b7917f1a724c";

// 1. 결제 위젯 렌더링
const paymentMethodsWidget = paymentWidget.renderPaymentMethods("#payment-method", { value: amount });
paymentWidget.renderAgreement("#agreement");

// 금액 수정 시 위젯 업데이트 버튼 이벤트
document.getElementById("apply-btn").addEventListener("click", () => {
    amount = parseInt(document.getElementById("order-amount-input").value);
    paymentMethodsWidget.updateAmount(amount);
    alert("결제 금액이 " + amount + "원으로 갱신되었습니다.");
});

// 결제하기 버튼 클릭
document.getElementById("payment-button").addEventListener("click", () => {
    const orderId = document.getElementById("order-id-input").value;
    const orderName = document.getElementById("order-name-input").value;

    paymentWidget.requestPayment({
        orderId: orderId,
        orderName: orderName,
        customerName: "주문자명",
        successUrl: window.location.origin + "/v1/payments/success",
        failUrl: window.location.origin + "/v1/payments/fail",
    }).catch(error => {
        alert(error.message);
    });
});