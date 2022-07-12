package com.duck.chess;

// 0x88 Representation of a chess board
/*
8 | 112 113 114 115 116 117 118 119
7 | 096 097 098 099 100 101 102 103
6 | 080 081 082 083 084 085 086 087
5 | 064 065 066 067 068 069 070 071
4 | 048 049 050 051 052 053 054 055
3 | 032 033 034 035 036 037 038 039
2 | 016 017 018 019 020 021 022 023
1 | 000 001 002 003 004 005 006 007
   ---------------------------------
     A   B   C   D   E   F   G   H
 */
public class Board {
    // Board Declaration
    public int[] board = new int[128];
    // Side to move, 0 = white, 1 = black
    public int side_to_move = 0;
    // Castle rights, four bits KQkq
    public int castle_rights = 0b1111;
    // En Passant square
    // Since EP square can never be 0 (must be on 3rd or 6th rank), we use 0 for NO_EP
    public int ep = 0;
    //Integer representing the move number
    public int movecount = 1;

    // Prettily print the board
    /*
        A B C D E F G H
       -----------------
    8 | r n b q k b n r | 8
    7 | p p p p p p p p | 7
    6 |                 | 6
    5 |                 | 5
    4 |                 | 4
    3 |                 | 3
    2 | P P P P P P P P | 2
    1 | R N B Q K B N R | 1
       -----------------
        A B C D E F G H

    Side to move: White
     */
    public void display() {
        System.out.println("    A B C D E F G H");
        System.out.println("   -----------------");
        for (int i = 7; i >= 0; i--) {
            System.out.print(Character.toUpperCase(Constants.RANKS[i]));
            System.out.print(" | ");
            for (int j = 0; j < 8; j++) {
                System.out.print(Constants.PIECE_TO_CHAR[board[i * 16 + j]] + " ");
            }
            System.out.print("| ");
            System.out.print(Character.toUpperCase(Constants.RANKS[i]));
            System.out.println();
        }
        System.out.println("   -----------------");
        System.out.println("    A B C D E F G H");

        System.out.println();
        System.out.println("Side to move: " + (side_to_move == Constants.COLOR_WHITE ? "White" : "Black"));
    }

    // Empty board
    public Board() {
    }

    // Parse FEN
    public Board(String fen) {
        var tokens = fen.split(" ");

        var board_string = tokens[0];

        var sq = Constants.A8;
        var inc = 0;
        for (var c : board_string.toCharArray()) {
            if (sq < 0) {
                break;
            }
            if (c == '/') {
                sq -= 16;
                inc = 0;
            } else if (c >= '1' && c <= '8') {
                inc += c - '0';
            } else {
                switch (c) {
                    case 'P' -> board[sq + inc] = Constants.PIECE_WHITE_PAWN;
                    case 'N' -> board[sq + inc] = Constants.PIECE_WHITE_KNIGHT;
                    case 'B' -> board[sq + inc] = Constants.PIECE_WHITE_BISHOP;
                    case 'R' -> board[sq + inc] = Constants.PIECE_WHITE_ROOK;
                    case 'Q' -> board[sq + inc] = Constants.PIECE_WHITE_QUEEN;
                    case 'K' -> board[sq + inc] = Constants.PIECE_WHITE_KING;
                    case 'p' -> board[sq + inc] = Constants.PIECE_BLACK_PAWN;
                    case 'n' -> board[sq + inc] = Constants.PIECE_BLACK_KNIGHT;
                    case 'b' -> board[sq + inc] = Constants.PIECE_BLACK_BISHOP;
                    case 'r' -> board[sq + inc] = Constants.PIECE_BLACK_ROOK;
                    case 'q' -> board[sq + inc] = Constants.PIECE_BLACK_QUEEN;
                    case 'k' -> board[sq + inc] = Constants.PIECE_BLACK_KING;
                    default -> {
                    }
                }
                inc++;
            }
        }

        var color = tokens[1].charAt(0);
        if (color == 'w') {
            side_to_move = Constants.COLOR_WHITE;
        } else {
            side_to_move = Constants.COLOR_BLACK;
        }

        var castle_rights_string = tokens[2];
        castle_rights = 0;
        for (var c : castle_rights_string.toCharArray()) {
            switch (c) {
                case 'K' -> castle_rights |= Constants.CASTLE_WHITE_K;
                case 'Q' -> castle_rights |= Constants.CASTLE_WHITE_Q;
                case 'k' -> castle_rights |= Constants.CASTLE_BLACK_K;
                case 'q' -> castle_rights |= Constants.CASTLE_BLACK_Q;
                default -> {
                }
            }
        }
        //Declare function/method for generating moves
        static void genLegalMoves() {
        	for(int i = 0; i < board.length; i++ ) {
        		if (side_to_move = 0) {
        			//Checks if there is a pawn 
        			if (board[i]= 1) {
        				if (board[i+16] = 0) {
        					System.out.println(FILES[i+16] + RANKS[i+16]);
        					if(board[i+32] = 0 && (16<=board[i]<=23)) {
        						System.out.println(FILES[square_file(i+32)] + RANKS[square_rank(i+32)]);
        					}
        					}
        				else if (board[i+15] != 0) {
        					System.out.println(FILES[i] + "x" + FILES[square_file(i+15)] + RANKS[square_rank(i+15)]);
        				}
        				else if (board[i+17] != 0) {
        					System.out.println(FILES[i] + "x" + FILES[square_file(i+17)] + RANKS[square_rank(i+17)]);
        				}
        				else if (112<= (i+16) <=119) {
        					System.out.println(FILES[square_file(i+16)] + RANKS[square_rank(i+16)] + "= Q");
        					System.out.println(FILES[square_file(i+16)] + RANKS[square_rank(i+16)] + "= N");
        					System.out.println(FILES[square_file(i+16)] + RANKS[square_rank(i+16)] + "= R");
        					System.out.println(FILES[square_file(i+16)] + RANKS[square_rank(i+16)] + "= B");
        				}
        				//Condition where pawn captures to promote
        				else if (board[i+15] != 0 && 112<= (i+15) <=119) {
        					System.out.println(FILES[square_file(i+16)]+ "x"+ FILES[square_file(i+15)] + RANKS[square_rank(i+15)] + "= Q");
        					System.out.println(FILES[square_file(i+16)]+ "x"+ FILES[square_file(i+15)] + RANKS[square_rank(i+15)] + "= N");
        					System.out.println(FILES[square_file(i+16)]+ "x"+ FILES[square_file(i+15)] + RANKS[square_rank(i+15)] + "= R");
        					System.out.println(FILES[square_file(i+16)]+ "x"+ FILES[square_file(i+15)] + RANKS[square_rank(i+15)] + "= B");
        					}
        				else if (board[i+17] != 0 && 112<= (i+17) <=119) {
            				System.out.println(FILES[square_file(i+16)]+ "x"+ FILES[square_file(i+17)] + RANKS[square_rank(i+17)] + "= Q");
            				System.out.println(FILES[square_file(i+16)]+ "x"+ FILES[square_file(i+17)] + RANKS[square_rank(i+17)] + "= N");
            				System.out.println(FILES[square_file(i+16)]+ "x"+ FILES[square_file(i+17)] + RANKS[square_rank(i+17)] + "= R");
            				System.out.println(FILES[square_file(i+16)]+ "x"+ FILES[square_file(i+17)] + RANKS[square_rank(i+17)] + "= B");
            				}
        				//En Passant, board.ep() will return what square the pawn will be able to capture on after checking if en passant is possible 
        				if(board.ep()=board[i+15]) {
        					System.out.println(FILES[square_file(i+16)]+ "x"+ FILES[square_file(i+15)] + RANKS[square_rank(i+15)]);
        				}
        				else if(board.ep()=board[i+17]) {
        					System.out.println(FILES[square_file(i+16)]+ "x"+ FILES[square_file(i+17)] + RANKS[square_rank(i+17)]);
        				}
        			}
        			//Knight
        			else if (board[i] = 2) {
        				System.out.println("N"+ FILES[square_file(i+18)] + RANKS[square_rank(i+18)]);
        				System.out.println("N"+ FILES[square_file(i-18)] + RANKS[square_rank(i-18)]);
        				System.out.println("N"+ FILES[square_file(i+33)] + RANKS[square_rank(i+33)]);
        				System.out.println("N"+ FILES[square_file(i-33)] + RANKS[square_rank(i-33)]);
        				System.out.println("N"+ FILES[square_file(i+31)] + RANKS[square_rank(i+31)]);
        				System.out.println("N"+ FILES[square_file(i-31)] + RANKS[square_rank(i-31)]);
        				System.out.println("N"+ FILES[square_file(i+14)] + RANKS[square_rank(i+14)]);
        				System.out.println("N"+ FILES[square_file(i-14)] + RANKS[square_rank(i-14)]);
        				if (board[i+18] != 0 ) {
        					System.out.println("Nx"+ FILES[square_file(i+18)] + RANKS[square_rank(i+18)]);
        				}
        				if (board[i-18] != 0 ) {
        					System.out.println("Nx"+ FILES[square_file(i-18)] + RANKS[square_rank(i-18)]);
        				}
        				else if (board[i+33] != 0 ) {
        					System.out.println("Nx"+ FILES[square_file(i+33)] + RANKS[square_rank(i+33)]);
        				}
        				else if (board[i-33] != 0 ) {
        					System.out.println("Nx"+ FILES[square_file(i-33)] + RANKS[square_rank(i-33)]);
        				}
        				else if (board[i+31] != 0 ) {
        					System.out.println("Nx"+ FILES[square_file(i+31)] + RANKS[square_rank(i+31)]);
        				}
        				else if (board[i-31] != 0 ) {
        					System.out.println("Nx"+ FILES[square_file(i-31)] + RANKS[square_rank(i-31)]);
        				}
        				else if (board[i+14] != 0 ) {
        					System.out.println("Nx"+ FILES[square_file(i+14)] + RANKS[square_rank(i+14)]);
        				}
        				else if (board[i-14] != 0 ) {
        					System.out.println("Nx"+ FILES[square_file(i-14)] + RANKS[square_rank(i-14)]);
        				}
        			}
        			//Bishop
        			else if (board[i] = 3) {
        				//Declares variable which can be manipulated to check if a square is open then checks if a piece occupies said square
        				int bishUpL = i;
        				while (board[bishUpL] = 0) {
        					bishUpL+=15;
        					if(board[bishUpL] = 0){
        						System.out.println("B" + FILES[square_file(bishUpL)] + RANKS[square_rank(bishUpL)]);
        					}	
        					else {
        						System.out.println("Bx" + FILES[square_file(bishUpL)] + RANKS[square_rank(bishUpL)]);
        						break 
        					}
        				}
        				//Same structure, goes Down Right Diagonal 
        				int bishDownR = i;
        				while (board[bishDownR] = 0) {
        					bishDownR-=15;
        					if(board[bishDownR] = 0){
        						System.out.println("B" + FILES[square_file(bishDownR)] + RANKS[square_rank(bishDownR)]);
        					}	
        					else {
        						System.out.println("Bx" + FILES[square_file(bishDownR)] + RANKS[square_rank(bishDownR)]);
        						break 
        					}
        				}
        				//Once again, this time checks Up Right diagonal
        				int bishUpR = i;   				
        				while (board[bishUpR] = 0) {
        					bishDownR+=17;
        					if(board[bishUpR] = 0){
        						System.out.println("B" + FILES[square_file(bishUpR)] + RANKS[square_rank(bishUpR)]);
        					}	
        					else {
        						System.out.println("Bx" + FILES[square_file(bishUpR)] + RANKS[square_rank(bishUpR)]);
        						break 
        					}
        				}
        				//Checks Down Left Diagonal
        				int bishDownL = i;
        				while (board[bishDownL] = 0) {
        					bishDownL+=17;
        					if(board[bishDownL] = 0){
        						System.out.println("B" + FILES[square_file(bishDownL)] + RANKS[square_rank(bishDownL)]);
        					}	
        					else {
        						System.out.println("Bx" + FILES[square_file(bishDownL)] + RANKS[square_rank(bishDownL)]);
        						break 
        					}
        				}
        			}
        				
        			//Rook, same process as bishop just sticks to rows and columns instead of diagonals
        			else if (board[i] = 4) {
        				int rookUp = i;
        				while (board[rookUp] = 0) {
        					rookUp+=16;
        					if(board[rookUp] = 0){
        						System.out.println("R" + FILES[square_file(rookUp)] + RANKS[square_rank(rookUp)]);
        					}	
        					else {
        						System.out.println("Rx" + FILES[square_file(rookUp)] + RANKS[square_rank(rookUp)]);
        						break 
        					}
        				}
        				int rookDown = i;
        				while (board[rookDown] = 0) {
        					rookUp-=16;
        					if(board[rookDown] = 0){
        						System.out.println("R" + FILES[square_file(rookDown)] + RANKS[square_rank(rookDown)]);
        					}	
        					else {
        						System.out.println("Rx" + FILES[square_file(rookDown)] + RANKS[square_rank(rookDown)]);
        						break 
        					}
        				}
        				int rookLeft = i;
        				while (board[rookLeft] = 0) {
        					rookLeft-=1;
        					if(board[rookLeft] = 0){
        						System.out.println("R" + FILES[square_file(rookLeft)] + RANKS[square_rank(rookLeft)]);
        					}	
        					else {
        						System.out.println("Rx" + FILES[square_file(rookLeft)] + RANKS[square_rank(rookLeft)]);
        						break 
        					}
        				}
        				int rookRight = i;
        				while (board[rookRight] = 0) {
        					rookLeft+=1;
        					if(board[rookRight] = 0){
        						System.out.println("R" + FILES[square_file(rookRight)] + RANKS[square_rank(rookRight)]);
        					}	
        					else {
        						System.out.println("Rx" + FILES[square_file(rookRight)] + RANKS[square_rank(rookRight)]);
        						break 
        					}
        				}
        			}
        			//Queen
        			else if (board[i]=5) {
        				int queenUpL = i;
        				while (board[queenUpL] = 0) {
        					queenUpL+=15;
        					if(board[queenUpL] = 0){
        						System.out.println("Q" + FILES[square_file(queenUpL)] + RANKS[square_rank(queenUpL)]);
        					}	
        					else {
        						System.out.println("Qx" + FILES[square_file(queenUpL)] + RANKS[square_rank(queenUpL)]);
        						break 
        					}
        				}
        				//Same structure, goes Down Right Diagonal 
        				int queenDownR = i;
        				while (board[queenDownR] = 0) {
        					queenDownR-=15;
        					if(board[queenDownR] = 0){
        						System.out.println("Q" + FILES[square_file(queenDownR)] + RANKS[square_rank(queenDownR)]);
        					}	
        					else {
        						System.out.println("Qx" + FILES[square_file(queenDownR)] + RANKS[square_rank(queenDownR)]);
        						break 
        					}
        				}
        				//Once again, this time checks Up Right diagonal
        				int queenUpR = i;   				
        				while (board[queenUpR] = 0) {
        					queenDownR+=17;
        					if(board[queenUpR] = 0){
        						System.out.println("Q" + FILES[square_file(queenUpR)] + RANKS[square_rank(queenUpR)]);
        					}	
        					else {
        						System.out.println("Qx" + FILES[square_file(queenUpR)] + RANKS[square_rank(queenUpR)]);
        						break 
        					}
        				}
        				//Checks Down Left Diagonal
        				int queenDownL = i;
        				while (board[queenDownL] = 0) {
        					queenDownR+=17;
        					if(board[queenDownL] = 0){
        						System.out.println("Q" + FILES[square_file(queenDownL)] + RANKS[square_rank(queenDownL)]);
        					}	
        					else {
        						System.out.println("Qx" + FILES[square_file(queenDownL)] + RANKS[square_rank(queenDownL)]);
        						break 
        					}
        				}
        				int queenUp = i;
        				while (board[queenUp] = 0) {
        					queenUp+=16;
        					if(board[queenUp] = 0){
        						System.out.println("Q" + FILES[square_file(queenUp)] + RANKS[square_rank(queenUp)]);
        					}	
        					else {
        						System.out.println("Qx" + FILES[square_file(queenUp)] + RANKS[square_rank(queenUp)]);
        						break 
        					}
        				}
        				int queenDown = i;
        				while (board[queenDown] = 0) {
        					queenUp-=16;
        					if(board[queenDown] = 0){
        						System.out.println("Q" + FILES[square_file(queenDown)] + RANKS[square_rank(queenDown)]);
        					}	
        					else {
        						System.out.println("Qx" + FILES[square_file(queenDown)] + RANKS[square_rank(queenDown)]);
        						break 
        					}
        				}
        				int queenLeft = i;
        				while (board[queenLeft] = 0) {
        					queenLeft-=1;
        					if(board[queenLeft] = 0){
        						System.out.println("Q" + FILES[square_file(queenLeft)] + RANKS[square_rank(queenLeft)]);
        					}	
        					else {
        						System.out.println("Qx" + FILES[square_file(queenLeft)] + RANKS[square_rank(queenLeft)]);
        						break 
        					}
        				}
        				int queenRight = i;
        				while (board[queenRight] = 0) {
        					rookLeft+=1;
        					if(board[queenRight] = 0){
        						System.out.println("Q" + FILES[square_file(queenRight)] + RANKS[square_rank(queenRight)]);
        					}	
        					else {
        						System.out.println("Qx" + FILES[square_file(queenRight)] + RANKS[square_rank(queenRight)]);
        						break 
        					}
        				}
        			}
        			//King
        			else if (board[i]=6) {
        				if(board[i+16] = 0) {
        					System.out.println("K" + FILES[square_file(i+16)] + RANKS[square_rank(i+16)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i+16)] + RANKS[square_rank(i+16)]);
        				}
        				if(board[i+17] = 0) {
        					System.out.println("K" + FILES[square_file(i+17)] + RANKS[square_rank(i+17)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i+17)] + RANKS[square_rank(i+17)]);
        				}
        				if(board[i-16] = 0) {
        					System.out.println("K" + FILES[square_file(i-16)] + RANKS[square_rank(i-16)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i-16)] + RANKS[square_rank(i-16)]);
        				}
        				if(board[i-17] = 0) {
        					System.out.println("K" + FILES[square_file(i-17)] + RANKS[square_rank(i-17)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i-17)] + RANKS[square_rank(i-17)]);
        				}
        				if(board[i+15] = 0) {
        					System.out.println("K" + FILES[square_file(i+15)] + RANKS[square_rank(i+15)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i+15)] + RANKS[square_rank(i+15)]);
        				}
        				if(board[i-15] = 0) {
        					System.out.println("K" + FILES[square_file(i-15)] + RANKS[square_rank(i-15)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i-15)] + RANKS[square_rank(i-15)]);
        				}
        				if(board[i+17] = 0) {
        					System.out.println("K" + FILES[square_file(i+17)] + RANKS[square_rank(i+17)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i+17)] + RANKS[square_rank(i+17)]);
        				}
        				if(board[i-17] = 0) {
        					System.out.println("K" + FILES[square_file(i-17)] + RANKS[square_rank(i-17)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i-17)] + RANKS[square_rank(i-17)]);
        				}
        				if(board[i+1] = 0) {
        					System.out.println("K" + FILES[square_file(i+1)] + RANKS[square_rank(i+1)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i+1)] + RANKS[square_rank(i+1)]);
        				}
        				if(board[i-1] = 0) {
        					System.out.println("K" + FILES[square_file(i-1)] + RANKS[square_rank(i-1)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i-1)] + RANKS[square_rank(i-1)])
        				}
        			}
        			} 
        		
        		//Black side move check
        		else if (side_to_move = 1) {
        			//Pawn code
        			if (board[i]= 1) {
        				if (board[i-16] = 0) {
        					System.out.println(FILES[i-16] + RANKS[i-16]);
        					if(board[i-32] = 0 && (96<=board[i]<=103)) {
        						System.out.println(FILES[square_file(i-32)] + RANKS[square_rank(i-32)]);
        					}
        					}
        				else if (board[i-15] != 0) {
        					System.out.println(FILES[i] + "x" + FILES[square_file(i-15)] + RANKS[square_rank(i-15)]);
        				}
        				else if (board[i-17] != 0) {
        					System.out.println(FILES[i] + "x" + FILES[square_file(i-17)] + RANKS[square_rank(i-17)]);
        				}
        				else if (0<= (i-16) <=7) {
        					System.out.println(FILES[square_file(i-16)] + RANKS[square_rank(i-16)] + "= Q");
        					System.out.println(FILES[square_file(i-16)] + RANKS[square_rank(i-16)] + "= N");
        					System.out.println(FILES[square_file(i-16)] + RANKS[square_rank(i-16)] + "= R");
        					System.out.println(FILES[square_file(i-16)] + RANKS[square_rank(i-16)] + "= B");
        				}
        				//Condition where pawn captures to promote
        				else if (board[i-15] != 0 && 0<= (i-15) <=7) {
        					System.out.println(FILES[square_file(i-16)]+ "x"+ FILES[square_file(i-15)] + RANKS[square_rank(i-15)] + "= Q");
        					System.out.println(FILES[square_file(i-16)]+ "x"+ FILES[square_file(i-15)] + RANKS[square_rank(i-15)] + "= N");
        					System.out.println(FILES[square_file(i-16)]+ "x"+ FILES[square_file(i-15)] + RANKS[square_rank(i-15)] + "= R");
        					System.out.println(FILES[square_file(i-16)]+ "x"+ FILES[square_file(i-15)] + RANKS[square_rank(i-15)] + "= B");
        					}
        				else if (board[i-17] != 0 && 0 <=(i-17)<= 7) {
            				System.out.println(FILES[square_file(i-16)]+ "x"+ FILES[square_file(i-17)] + RANKS[square_rank(i-17)] + "= Q");
            				System.out.println(FILES[square_file(i-16)]+ "x"+ FILES[square_file(i-17)] + RANKS[square_rank(i-17)] + "= N");
            				System.out.println(FILES[square_file(i-16)]+ "x"+ FILES[square_file(i-17)] + RANKS[square_rank(i-17)] + "= R");
            				System.out.println(FILES[square_file(i-16)]+ "x"+ FILES[square_file(i-17)] + RANKS[square_rank(i-17)] + "= B");
            				}
        				//En Passant, board.ep() will return what square the pawn will be able to capture on after checking if en passant is possible 
        				if(board.ep()=board[i-15]) {
        					System.out.println(FILES[square_file(i-16)]+ "x"+ FILES[square_file(i-15)] + RANKS[square_rank(i-15)]);
        				}
        				else if(board.ep()=board[i-17]) {
        					System.out.println(FILES[square_file(i+16)]+ "x"+ FILES[square_file(i-17)] + RANKS[square_rank(i-17)]);
        				}
        			}
        			//Knight
        			else if (board[i] = 2) {
        				System.out.println("N"+ FILES[square_file(i+18)] + RANKS[square_rank(i+18)]);
        				System.out.println("N"+ FILES[square_file(i-18)] + RANKS[square_rank(i-18)]);
        				System.out.println("N"+ FILES[square_file(i+33)] + RANKS[square_rank(i+33)]);
        				System.out.println("N"+ FILES[square_file(i-33)] + RANKS[square_rank(i-33)]);
        				System.out.println("N"+ FILES[square_file(i+31)] + RANKS[square_rank(i+31)]);
        				System.out.println("N"+ FILES[square_file(i-31)] + RANKS[square_rank(i-31)]);
        				System.out.println("N"+ FILES[square_file(i+14)] + RANKS[square_rank(i+14)]);
        				System.out.println("N"+ FILES[square_file(i-14)] + RANKS[square_rank(i-14)]);
        				if (board[i+18] != 0 ) {
        					System.out.println("Nx"+ FILES[square_file(i+18)] + RANKS[square_rank(i+18)]);
        				}
        				if (board[i-18] != 0 ) {
        					System.out.println("Nx"+ FILES[square_file(i-18)] + RANKS[square_rank(i-18)]);
        				}
        				else if (board[i+33] != 0 ) {
        					System.out.println("Nx"+ FILES[square_file(i+33)] + RANKS[square_rank(i+33)]);
        				}
        				else if (board[i-33] != 0 ) {
        					System.out.println("Nx"+ FILES[square_file(i-33)] + RANKS[square_rank(i-33)]);
        				}
        				else if (board[i+31] != 0 ) {
        					System.out.println("Nx"+ FILES[square_file(i+31)] + RANKS[square_rank(i+31)]);
        				}
        				else if (board[i-31] != 0 ) {
        					System.out.println("Nx"+ FILES[square_file(i-31)] + RANKS[square_rank(i-31)]);
        				}
        				else if (board[i+14] != 0 ) {
        					System.out.println("Nx"+ FILES[square_file(i+14)] + RANKS[square_rank(i+14)]);
        				}
        				else if (board[i-14] != 0 ) {
        					System.out.println("Nx"+ FILES[square_file(i-14)] + RANKS[square_rank(i-14)]);
        				}
        			}
        			//Bishop
        			else if (board[i] = 3) {
        				//Declares variable which can be manipulated to check if a square is open then checks if a piece occupies said square
        				int bishUpL = i;
        				while (board[bishUpL] = 0) {
        					bishUpL+=15;
        					if(board[bishUpL] = 0){
        						System.out.println("B" + FILES[square_file(bishUpL)] + RANKS[square_rank(bishUpL)]);
        					}	
        					else {
        						System.out.println("Bx" + FILES[square_file(bishUpL)] + RANKS[square_rank(bishUpL)]);
        						break 
        					}
        				}
        				//Same structure, goes Down Right Diagonal 
        				int bishDownR = i;
        				while (board[bishDownR] = 0) {
        					bishDownR-=15;
        					if(board[bishDownR] = 0){
        						System.out.println("B" + FILES[square_file(bishDownR)] + RANKS[square_rank(bishDownR)]);
        					}	
        					else {
        						System.out.println("Bx" + FILES[square_file(bishDownR)] + RANKS[square_rank(bishDownR)]);
        						break 
        					}
        				}
        				//Once again, this time checks Up Right diagonal
        				int bishUpR = i;   				
        				while (board[bishUpR] = 0) {
        					bishDownR+=17;
        					if(board[bishUpR] = 0){
        						System.out.println("B" + FILES[square_file(bishUpR)] + RANKS[square_rank(bishUpR)]);
        					}	
        					else {
        						System.out.println("Bx" + FILES[square_file(bishUpR)] + RANKS[square_rank(bishUpR)]);
        						break 
        					}
        				}
        				//Checks Down Left Diagonal
        				int bishDownL = i;
        				while (board[bishDownL] = 0) {
        					bishDownR+=17;
        					if(board[bishDownL] = 0){
        						System.out.println("B" + FILES[square_file(bishDownL)] + RANKS[square_rank(bishDownL)]);
        					}	
        					else {
        						System.out.println("Bx" + FILES[square_file(bishDownL)] + RANKS[square_rank(bishDownL)]);
        						break 
        					}
        				}
        			}
        				
        			//Rook, same process as bishop just sticks to rows and columns instead of diagonals
        			else if (board[i] = 4) {
        				int rookUp = i;
        				while (board[rookUp] = 0) {
        					rookUp+=16;
        					if(board[rookUp] = 0){
        						System.out.println("R" + FILES[square_file(rookUp)] + RANKS[square_rank(rookUp)]);
        					}	
        					else {
        						System.out.println("Rx" + FILES[square_file(rookUp)] + RANKS[square_rank(rookUp)]);
        						break 
        					}
        				}
        				int rookDown = i;
        				while (board[rookDown] = 0) {
        					rookUp-=16;
        					if(board[rookDown] = 0){
        						System.out.println("R" + FILES[square_file(rookDown)] + RANKS[square_rank(rookDown)]);
        					}	
        					else {
        						System.out.println("Rx" + FILES[square_file(rookDown)] + RANKS[square_rank(rookDown)]);
        						break 
        					}
        				}
        				int rookLeft = i;
        				while (board[rookLeft] = 0) {
        					rookLeft-=1;
        					if(board[rookLeft] = 0){
        						System.out.println("R" + FILES[square_file(rookLeft)] + RANKS[square_rank(rookLeft)]);
        					}	
        					else {
        						System.out.println("Rx" + FILES[square_file(rookLeft)] + RANKS[square_rank(rookLeft)]);
        						break 
        					}
        				}
        				int rookRight = i;
        				while (board[rookRight] = 0) {
        					rookLeft+=1;
        					if(board[rookRight] = 0){
        						System.out.println("R" + FILES[square_file(rookRight)] + RANKS[square_rank(rookRight)]);
        					}	
        					else {
        						System.out.println("Rx" + FILES[square_file(rookRight)] + RANKS[square_rank(rookRight)]);
        						break 
        					}
        				}
        			}
        			//Queen
        			else if (board[i]=5) {
        				int queenUpL = i;
        				while (board[queenUpL] = 0) {
        					queenUpL+=15;
        					if(board[queenUpL] = 0){
        						System.out.println("Q" + FILES[square_file(queenUpL)] + RANKS[square_rank(queenUpL)]);
        					}	
        					else {
        						System.out.println("Qx" + FILES[square_file(queenUpL)] + RANKS[square_rank(queenUpL)]);
        						break 
        					}
        				}
        				//Same structure, goes Down Right Diagonal 
        				int queenDownR = i;
        				while (board[queenDownR] = 0) {
        					queenDownR-=15;
        					if(board[queenDownR] = 0){
        						System.out.println("Q" + FILES[square_file(queenDownR)] + RANKS[square_rank(queenDownR)]);
        					}	
        					else {
        						System.out.println("Qx" + FILES[square_file(queenDownR)] + RANKS[square_rank(queenDownR)]);
        						break 
        					}
        				}
        				//Once again, this time checks Up Right diagonal
        				int queenUpR = i;   				
        				while (board[queenUpR] = 0) {
        					queenDownR+=17;
        					if(board[queenUpR] = 0){
        						System.out.println("Q" + FILES[square_file(queenUpR)] + RANKS[square_rank(queenUpR)]);
        					}	
        					else {
        						System.out.println("Qx" + FILES[square_file(queenUpR)] + RANKS[square_rank(queenUpR)]);
        						break 
        					}
        				}
        				//Checks Down Left Diagonal
        				int queenDownL = i;
        				while (board[queenDownL] = 0) {
        					queenDownR+=17;
        					if(board[queenDownL] = 0){
        						System.out.println("Q" + FILES[square_file(queenDownL)] + RANKS[square_rank(queenDownL)]);
        					}	
        					else {
        						System.out.println("Qx" + FILES[square_file(queenDownL)] + RANKS[square_rank(queenDownL)]);
        						break 
        					}
        				}
        				int queenUp = i;
        				while (board[queenUp] = 0) {
        					queenUp+=16;
        					if(board[queenUp] = 0){
        						System.out.println("Q" + FILES[square_file(queenUp)] + RANKS[square_rank(queenUp)]);
        					}	
        					else {
        						System.out.println("Qx" + FILES[square_file(queenUp)] + RANKS[square_rank(queenUp)]);
        						break 
        					}
        				}
        				int queenDown = i;
        				while (board[queenDown] = 0) {
        					queenUp-=16;
        					if(board[queenDown] = 0){
        						System.out.println("Q" + FILES[square_file(queenDown)] + RANKS[square_rank(queenDown)]);
        					}	
        					else {
        						System.out.println("Qx" + FILES[square_file(queenDown)] + RANKS[square_rank(queenDown)]);
        						break 
        					}
        				}
        				int queenLeft = i;
        				while (board[queenLeft] = 0) {
        					queenLeft-=1;
        					if(board[queenLeft] = 0){
        						System.out.println("Q" + FILES[square_file(queenLeft)] + RANKS[square_rank(queenLeft)]);
        					}	
        					else {
        						System.out.println("Qx" + FILES[square_file(queenLeft)] + RANKS[square_rank(queenLeft)]);
        						break 
        					}
        				}
        				int queenRight = i;
        				while (board[queenRight] = 0) {
        					rookLeft+=1;
        					if(board[queenRight] = 0){
        						System.out.println("Q" + FILES[square_file(queenRight)] + RANKS[square_rank(queenRight)]);
        					}	
        					else {
        						System.out.println("Qx" + FILES[square_file(queenRight)] + RANKS[square_rank(queenRight)]);
        						break 
        					}
        				}
        			}
        			//King
        			else if (board[i]=6) {
        				if(board[i+16] = 0) {
        					System.out.println("K" + FILES[square_file(i+16)] + RANKS[square_rank(i+16)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i+16)] + RANKS[square_rank(i+16)]);
        				}
        				if(board[i+17] = 0) {
        					System.out.println("K" + FILES[square_file(i+17)] + RANKS[square_rank(i+17)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i+17)] + RANKS[square_rank(i+17)]);
        				}
        				if(board[i-16] = 0) {
        					System.out.println("K" + FILES[square_file(i-16)] + RANKS[square_rank(i-16)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i-16)] + RANKS[square_rank(i-16)]);
        				}
        				if(board[i-17] = 0) {
        					System.out.println("K" + FILES[square_file(i-17)] + RANKS[square_rank(i-17)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i-17)] + RANKS[square_rank(i-17)]);
        				}
        				if(board[i+15] = 0) {
        					System.out.println("K" + FILES[square_file(i+15)] + RANKS[square_rank(i+15)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i+15)] + RANKS[square_rank(i+15)]);
        				}
        				if(board[i-15] = 0) {
        					System.out.println("K" + FILES[square_file(i-15)] + RANKS[square_rank(i-15)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i-15)] + RANKS[square_rank(i-15)]);
        				}
        				if(board[i+17] = 0) {
        					System.out.println("K" + FILES[square_file(i+17)] + RANKS[square_rank(i+17)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i+17)] + RANKS[square_rank(i+17)]);
        				}
        				if(board[i-17] = 0) {
        					System.out.println("K" + FILES[square_file(i-17)] + RANKS[square_rank(i-17)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i-17)] + RANKS[square_rank(i-17)]);
        				}
        				if(board[i+1] = 0) {
        					System.out.println("K" + FILES[square_file(i+1)] + RANKS[square_rank(i+1)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i+1)] + RANKS[square_rank(i+1)]);
        				}
        				if(board[i-1] = 0) {
        					System.out.println("K" + FILES[square_file(i-1)] + RANKS[square_rank(i-1)]);
        				}
        				else {
        					System.out.println("Kx" + FILES[square_file(i-1)] + RANKS[square_rank(i-1)])
        				}
        		}
        		}
        	}
        }
    }
}
