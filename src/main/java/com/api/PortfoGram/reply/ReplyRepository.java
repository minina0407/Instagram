package com.api.PortfoGram.reply;

import com.api.PortfoGram.reply.entity.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {
}
