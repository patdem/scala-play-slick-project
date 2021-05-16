-- !Ups

CREATE TABLE "category" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name" VARCHAR NOT NULL
);

CREATE TABLE "product" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name" VARCHAR NOT NULL,
    "description" TEXT NOT NULL,
    "category" INTEGER NOT NULL,
    FOREIGN KEY(category) references category(id)
);

CREATE TABLE "userInfo" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "email" VARCHAR NOT NULL,
    "nickname" VARCHAR NOT NULL,
    "password" VARCHAR NOT NULL
);

CREATE TABLE "userAddress" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "userId" INTEGER NOT NULL,
    "firstname" VARCHAR NOT NULL,
    "lastname" VARCHAR NOT NULL,
    "address" VARCHAR NOT NULL,
    "zipcode" VARCHAR NOT NULL,
    "city" VARCHAR NOT NULL,
    "country" VARCHAR NOT NULL,
    FOREIGN KEY(userId) references userInfo(id)
);

CREATE TABLE "creditCard" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "userId" INTEGER NOT NULL,
    "cardName" VARCHAR NOT NULL,
    "cardNumber" VARCHAR NOT NULL,
    "expDate" VARCHAR NOT NULL,
    "cvcCode" VARCHAR NOT NULL,
    FOREIGN KEY(userId) references userInfo(id)
);

CREATE TABLE "payment" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "userId" INTEGER NOT NULL,
    "creditCardId" INTEGER NOT NULL,
    "amount" INTEGER NOT NULL,
    FOREIGN KEY(userId) references userInfo(id),
    FOREIGN KEY(creditCardId) references creditCard(id)
);

CREATE TABLE "voucher" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "amount" INTEGER NOT NULL
);

CREATE TABLE "promoCode" (
     "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "name" VARCHAR NOT NULL,
     "amount" INTEGER NOT NULL
);

CREATE TABLE "order" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "userId" INTEGER NOT NULL,
    "paymentId" VARCHAR NOT NULL,
    "voucherId" INTEGER NOT NULL,
    "promoCodeId" INTEGER NOT NULL,
    FOREIGN KEY(userId) references userInfo(id),
    FOREIGN KEY(paymentId) references payment(id),
    FOREIGN KEY(voucherId) references voucher(id),
    FOREIGN KEY(promoCodeId) references promoCode(id)
);

CREATE TABLE "basket" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "userId" INTEGER NOT NULL,
    "productId" VARCHAR NOT NULL,
    FOREIGN KEY(userId) references userInfo(id),
    FOREIGN KEY(productId) references product(id)
);

-- !Downs

DROP TABLE "category";
DROP TABLE "product";
DROP TABLE "userInfo";
DROP TABLE "userAddress";
DROP TABLE "creditCard";
DROP TABLE "payment";
DROP TABLE "voucher";
DROP TABLE "promoCode";
DROP TABLE "order";
DROP TABLE "basket";