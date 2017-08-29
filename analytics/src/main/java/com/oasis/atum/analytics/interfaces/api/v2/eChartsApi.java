package com.oasis.atum.analytics.interfaces.api.v2;


import com.oasis.atum.base.infrastructure.util.Restful;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.joda.time.LocalDate;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.stream.Stream;

import static com.oasis.atum.base.infrastructure.util.DateUtil.getWeekDays;

/**
 * 图表
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("v2")
public class eChartsApi
{
	private final JdbcTemplate template;

	@GetMapping("orders/success")
	public Mono<ResponseEntity> ordersSuccess()
	{
		log.info("本周每天成单量");

		//本周每天成单量
		val list = template.queryForList(
				"SELECT LEFT(createtime,10) date ,count(*) count\n" +
						"FROM crm_serve_order\n" +
						"WHERE\n" +
						"YEARWEEK(DATE_FORMAT(createtime,'%Y-%m-%d')) = YEARWEEK(NOW()) -1\n" +
						"AND status IN (3502,3601)\n" +
						"GROUP BY LEFT(createtime,10)\n" +
						"ORDER BY createtime DESC");

		//本周日期
		return Flux.fromStream(getWeekDays())
							 .flatMap(d ->
									 {
										 val data = Flux.fromIterable(list)
																		//有数据的天数
																		.filter(m -> d.isEqual(new LocalDate(m.get("date"))));
										 //无数据天数默认0
										 val empty = new HashMap<String, Object>();
										 empty.put("date", d.toString("yyyy-MM-dd"));
										 empty.put("count", 0);
										 return data.defaultIfEmpty(empty);
									 }
							 )
							 .collectList()
							 .map(Restful::ok);
	}

	@GetMapping("orders/money")
	public Mono<ResponseEntity> ordersMoney()
	{
		log.info("本周每日订单金额");

		//本周每日订单金额
		val list = template.queryForList(
				"SELECT LEFT(createtime,10) date ,SUM(chargemoney) charge ,SUM(actualmoney) actual\n" +
						"FROM crm_serve_order\n" +
						"WHERE YEARWEEK(DATE_FORMAT(createtime,'%Y-%m-%d')) = YEARWEEK(NOW()) -1\n" +
						"AND status IN (3502,3601)\n" +
						"GROUP BY LEFT(createtime,10)\n" +
						"ORDER BY createtime DESC"
		);

		//本周日期
		return Flux.fromStream(getWeekDays())
							 .flatMap(d ->
									 {
										 val data = Flux.fromIterable(list)
																		//有数据的天数
																		.filter(m -> d.isEqual(new LocalDate(m.get("date"))));
										 //无数据天数默认0
										 val empty = new HashMap<String, Object>();
										 empty.put("date", d.toString("yyyy-MM-dd"));
										 empty.put("charge", 1);
										 empty.put("actual", 0);
										 return data.defaultIfEmpty(empty);
									 }
							 )
							 .collectList()
							 .map(Restful::ok);
	}

	public static void main(String[] args)
	{
		Stream.of(1, 3, 5, 7, 9, 2)
				.filter(i -> i >= 5)
				.map(i -> i * 2 + "")
				.forEach(System.out::println);
	}
}
