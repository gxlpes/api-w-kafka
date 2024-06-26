package com.alura.pix.consumidor;

import com.alura.pix.dto.PixDTO;
import com.alura.pix.dto.PixStatus;
import com.alura.pix.model.Key;
import com.alura.pix.model.Pix;
import com.alura.pix.repository.KeyRepository;
import com.alura.pix.repository.PixRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PixValidator {

    private final KeyRepository keyRepository;

    private final PixRepository pixRepository;

    public PixValidator(KeyRepository keyRepository, PixRepository pixRepository) {
        this.keyRepository = keyRepository;
        this.pixRepository = pixRepository;
    }

    @KafkaListener(topics = "pix-topic-partitions", groupId = "grupo")
    public void processaPix(PixDTO pixDTO) {
        System.out.println("Pix recebido" + pixDTO.getIdentifier());

        Pix pix = pixRepository.findByIdentifier(pixDTO.getIdentifier());

        Key origem = keyRepository.findByChave(pixDTO.getChaveOrigem());
        Key destino = keyRepository.findByChave(pixDTO.getChaveDestino());

        if(origem != null && destino != null) {
            pix.setStatus(PixStatus.PROCESSADO);
        } else {
            pix.setStatus(PixStatus.ERRO);
        }

        pixRepository.save(pix);
    }

}
