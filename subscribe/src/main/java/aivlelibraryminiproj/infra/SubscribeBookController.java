package aivlelibraryminiproj.infra;

import aivlelibraryminiproj.domain.*;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/subscribeBooks")
@Transactional
public class SubscribeBookController {

    @Autowired
    SubscribeBookRepository subscribeBookRepository;
    
    @Autowired // CheckBookRepository 주입
    CheckBookRepository checkBookRepository;
    
    @PostMapping("/subscribeBooks")
    public SubscribeBook createSubscription(@RequestBody SubscribeBook subscribeBook) {
        System.out.println("##### /subscribebooks POST 요청이 접수되었습니다. #####");

        // 1. 요청된 bookId의 유효성 검사
        Long bookIdToCheck = subscribeBook.getBookId();
        Long subscriberIdToCheck = subscribeBook.getSubscriberId();
        if (bookIdToCheck == null || subscriberIdToCheck == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book ID와 SubscriberId는 필수 값입니다.");
        }
        
        // 이미 구독 중인지 확인
        boolean alreadySubscribed = subscribeBookRepository.existsBySubscriberIdAndBookId(subscriberIdToCheck, bookIdToCheck);
        if (alreadySubscribed) {
            System.out.println("##### 구독자가 이미 bookId: " + bookIdToCheck + " 를 구독 중입니다. #####");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 구독 중인 책입니다.");
        }

        // 2. 읽기 모델(CheckBook)에서 bookId 존재 여부 확인
        Optional<CheckBook> optionalCheckBook = checkBookRepository.findById(bookIdToCheck);

        if (!optionalCheckBook.isPresent()) {
            // 3. 책이 존재하지 않으면 404 Not Found 응답 반환
            System.out.println("##### bookId: " + bookIdToCheck + " 에 해당하는 책이 읽기 모델에 존재하지 않아 구독 요청을 취소합니다. #####");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID: " + bookIdToCheck + " 에 해당하는 책을 찾을 수 없습니다.");
        }

        // 4. 책이 존재하면 구독 정보 저장 진행
        System.out.println("##### bookId: " + bookIdToCheck + " 에 해당하는 책을 찾았습니다. 구독을 진행합니다. #####");
        CheckBook foundBook = optionalCheckBook.get(); // Optional에서 실제 CheckBook 객체를 가져옵니다.
        subscribeBook.setTitle(foundBook.getTitle()); // 가져온 CheckBook 객체에서 title을 설정합니다.
        subscribeBook.setIsBestSeller(foundBook.getIsBestSeller());// 가져온 CheckBook 객체에서 베스트셀러 여부을 설정합니다.
        subscribeBook.setSubscriptionFee(foundBook.getSubscriptionFee());// 가져온 CheckBook 객체에서 subscriptionFee을 설정합니다.

        return subscribeBookRepository.save(subscribeBook);
    }
}
//>>> Clean Arch / Inbound Adaptor