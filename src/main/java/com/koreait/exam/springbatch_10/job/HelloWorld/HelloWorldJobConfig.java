package com.koreait.exam.springbatch_10.job.HelloWorld;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HelloWorldJobConfig   {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean // 프로그램 시작 시 태어남
    public Job helloWorldJob() {
        return jobBuilderFactory.get("helloWorldJob")
//                .incrementer(new RunIdIncrementer()) // 강제로 매번 다른 ID를 실행할 때 파라미터로 부여
                .start(helloWorldStep1())
                .next(helloWorldStep2())
                .build();
    }

    @Bean
    @JobScope // 생명주기 설정: 이 Step은 Job이 실행될 때 생성되고, Job이 끝나면 Step도 사라짐
    public Step helloWorldStep1() {
        return stepBuilderFactory
                .get("helloWorldStep1")
                .tasklet(helloWorldStep1Tasklet())
                .build();
    }

    @Bean
    @StepScope // 생명주기 설정: 이 Tasklet은 Step이 실행될 때 생성되고, Step이 종료되면 사라짐

    public Tasklet helloWorldStep1Tasklet() {
        return (stepContribution, chunkContext) -> {
            System.out.println("헬로월드 111111111111111!!!");
            return RepeatStatus.FINISHED;
        };
    }
    @Bean
    @JobScope
    public Step helloWorldStep2() {
        return stepBuilderFactory
                .get("helloWorldStep2")
                .tasklet(helloWorldStep2Tasklet())
                .build();
    }
    @Bean
    @StepScope
    public Tasklet helloWorldStep2Tasklet() {
        return (stepContribution, chunkContext) -> {
            System.out.println("헬로월드 222222222222!!!");

            // 테스트용으로 실패시키기
            // 하나라도 실패하면 그 Job은 실패한 잡이 되는 것
            if(false){
                throw new Exception("실패 : 헬로월드 태스클릿 2 실패");
            }

            return RepeatStatus.FINISHED;
        };
    }
/*
정산 프로그램 만들기

온라인 쇼핑몰

ebook: 실물 X => 실물이 있는 것에 비해 데이터 구축이 비교적 쉽다 => 품절될 리가 없음 => 상품(번호, 트렌드 코리아 2025, 가격, 출판사, 작가, ...)

의류: 실물 O => 데이터 구축이 어렵다 => 상품(번호, 이름, 옵션(RED/95, RED/100, RED/105, ...,  BLACK/95, BLACK/100...), 창고, 재고, 품절, 하자(반품), 교환, 배송, 송장번호, 발송, 수신날짜

 - 실물 o

*/

}