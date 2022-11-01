package management.test

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get


@Controller("/test")
class Test(
    private val selfClient : SelfClient
) {



    @Get("/step3test")
    fun push() {
        println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
        return
        //val data = """{"contractData":{"organizationInfo":{"unpN":"191832203","orgF":"IP","organization":"Моров Андрей Михайлович","urAddress":{"index":"123","address":"Минск"},"postAddress":{"index":"123","address":"Минск"},"mail":"test@gmail.com","phone":"+123","positionClient":"","fioClient":"","docType":"","skkoNumber":"1111 1111 1111 1111"},"bankInfo":{"payment":"BY20OLDD00001212000012122321","BICBank":"sdfds","nameBank":"sdf"},"tradeInfo":[{"torgName":"Магазин","torgAddress":"Минск","timeWork":[{"time":{"startTime":"10:00","closeTime":"00:00"},"days":[1]}],"strTimeWork":["Пн.: 10:00-00:00"],"torgType":"Киоск","unitsCashbox":"1"}, [{"torgName":"Мастерплюс ООО","torgAddress":"г. Минск, Северный переулок 13, Западная улица 7","timeWork":[{"time":"Full_Time","days":[1,2,3,4,5,6,7]},{"time":{"startTime":"10:00","closeTime":"12:00"},"days":[1,2,3,4,5,6,7]}],"strTimeWork":["Пн., Вт., Ср., Чт., Пт., Сб., Вс.: Круглосуточно","Пн., Вт., Ср., Чт., Пт., Сб., Вс.: 10:00-12:00"],"torgType":"Магазин","unitsCashbox":"5"}]]},"equipment":{"app":"iKassa Smart","printerCount":5,"printerModel":null,"units":10}}"""
        val data2 = """
{
  "contractData": {
    "organizationInfo": {
      "unpN": "123213213",
      "orgF": "IP",
      "organization": "Моров Андрей Михайлович",
      "urAddress": {
        "index": "220036",
        "address": "г. Минск, Северный переулок 13, Западная улица 7"
      },
      "postAddress": {
        "index": "220030",
        "address": "г. Минск, Северный переулок 13, Западная улица 10"
      },
      "mail": "test@musicaltheatre.by",
      "phone": "+375172008126",
      "positionClient": "",
      "fioClient": "",
      "fioClient2": "Морова Андрея Михайловича",
      "docType": "",
      "skkoNumber": ""
    },
    "bankInfo": {
      "payment": "BYOLMP1234000043210000123412",
      "BICBank": "sdfds",
      "nameBank": "sdf"
    },
    "tradeInfo": [
      {
        "torgName": "Пинта",
        "torgAddress": "ул. Берсона 16, Минск",
        "timeWork": [
          {
            "time": {
              "startTime": "10:00",
              "closeTime": "00:00"
            },
            "days": [
              1
            ]
          }
        ],
        "strTimeWork": [
          "Пн.: 10:00-00:00"
        ],
        "torgType": "Киоск",
        "unitsCashbox": "2"
      },
      {
        "torgName": "Мастерплюс ООО",
        "torgAddress": "г. Минск, Северный переулок 13, Западная улица 7",
        "timeWork": [
          {
            "time": "Full_Time",
            "days": [
              1,
              2,
              3,
              4,
              5,
              6,
              7
            ]
          },
          {
            "time": {
              "startTime": "10:00",
              "closeTime": "12:00"
            },
            "days": [
              1,
              2,
              3,
              4,
              5,
              6,
              7
            ]
          }
        ],
        "strTimeWork": [
          "Пн., Вт., Ср., Чт., Пт., Сб., Вс.: Круглосуточно",
          "Пн., Вт., Ср., Чт., Пт., Сб., Вс.: 10:00-12:00"
        ],
        "torgType": "Магазин",
        "unitsCashbox": "4"
      }
    ]
  },
  "equipment": {
    "solution": "smart_and_card_azur",
    "printerCount": 2,
    "printerModel": null,
    "period": 6,
    "units": 6
  }
}
""".trimIndent()
        val result = selfClient.post("/docs/step3", data2)
        println(result)
        println(data2)
    }
}