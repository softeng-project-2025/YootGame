package model.dto;

import model.player.Player;

public record PlayerDto(
        int id,
        String name,
        boolean isCurrent
) {
    public static PlayerDto from(Player player, boolean isCurrent) {
        return new PlayerDto(
                player.getId(),
                player.getName(),
                isCurrent
        );
    }
}
