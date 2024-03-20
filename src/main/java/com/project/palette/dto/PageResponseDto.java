package com.project.palette.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class PageResponseDto<E> {

    private List<?> dtoList;
    private List<Integer> pageNumList;
    private PageRequestDto pageRequestDto;
    private boolean prev, next;
    private int totalCnt, totalPageCnt;
    private int prevPageNo, nextPageNo;

    @Builder(builderMethodName = "withAll")
    public PageResponseDto(List<E> dtoList, PageRequestDto pageRequestDto, long total) {
        this.dtoList = dtoList;
        this.pageRequestDto = pageRequestDto;
        this.totalCnt = (int) total;

        int size = pageRequestDto.getSize();
        int end = (int) (Math.ceil(pageRequestDto.getPage() / (double) size)) * size;
        int start = end - 9;
        int last = (int) Math.ceil(totalCnt / (double) size);
        end = end > last ? last : end;

        this.prev = start > 1;
        this.next = totalCnt > end * pageRequestDto.getPage();
        this.pageNumList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
        this.prevPageNo = prev ? start - 1 : 0;
        this.nextPageNo = next ? end + 1 : 0;
    }
}
