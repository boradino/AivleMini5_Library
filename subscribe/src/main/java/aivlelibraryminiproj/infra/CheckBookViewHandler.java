package aivlelibraryminiproj.infra;

import aivlelibraryminiproj.config.kafka.KafkaProcessor;
import aivlelibraryminiproj.domain.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.hibernate.annotations.Check;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class CheckBookViewHandler {

    //<<< DDD / CQRS
    @Autowired
    private CheckBookRepository checkBookRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenBookRegistered_then_CREATE_1(
        @Payload BookRegistered BookRegistered
    ) {
        try {
            if (!BookRegistered.validate()) return;

            // view 객체 생성
            CheckBook checkBook = new CheckBook();
            // view 객체에 이벤트의 Value 를 set 함
            checkBook.setId(BookRegistered.getId());
            checkBook.setTitle(BookRegistered.getTitle());
            checkBook.setAuthorId(BookRegistered.getAuthorId());
            checkBook.setSubscriptionFee(BookRegistered.getSubscriptionFee());
            checkBook.setIsBestSeller(BookRegistered.getIsBestSeller());
            // view 레파지 토리에 save
            checkBookRepository.save(checkBook);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenBestSellerArchived_then_UPDATE_1(
        @Payload BestSellerArchived bestSellerArchived
    ){
        try{
            if(!bestSellerArchived.validate()) return;

            Optional <CheckBook> checkBookOptional = checkBookRepository.findById(bestSellerArchived.getId());

            if(checkBookOptional.isPresent()){
                CheckBook checkBook = checkBookOptional.get();
                checkBook.setIsBestSeller(bestSellerArchived.getIsBestSeller());

                if(bestSellerArchived.getIsBestSeller()){
                    checkBook.setSubscriptionFee(1500);
                }else{
                    checkBook.setSubscriptionFee(1000);
                }

                checkBookRepository.save(checkBook);

            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenBestSellerCancelled_then_UPDATE_2(
        @Payload BestSellerCancelled bestSellerCancelled
    ){
        try{
            if(!bestSellerCancelled.validate()) return;

            Optional <CheckBook> checkBookOptional = checkBookRepository.findById(bestSellerCancelled.getId());

            if(checkBookOptional.isPresent()){
                CheckBook checkBook = checkBookOptional.get();
                checkBook.setIsBestSeller(bestSellerCancelled.getIsBestSeller());

                if(bestSellerCancelled.getIsBestSeller()){
                    checkBook.setSubscriptionFee(1500);
                }else{
                    checkBook.setSubscriptionFee(1000);
                }

                checkBookRepository.save(checkBook);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenBookDeleted_then_DELETE_1(
        @Payload BookDeleted bookDeleted
    ) {
        try {
            if (!bookDeleted.validate()) return;
            // view 레파지 토리에 삭제 쿼리
            checkBookRepository.deleteById(bookDeleted.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //>>> DDD / CQRS
}
