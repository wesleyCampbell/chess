# TODO

- [ x ] Implement em passant pawn special move
- [ x ] Totally refactor code to allow ChessBoard to use its own iterator
- [ ] Figure out ChessBoardGenerator refactor
        - Different Chess Board shapes
        - File input reading for different formats
        - Barrier piece type vs void piece type (functionally equivilant, renders different)
- [ ] Make object flow chart to figure how the whole game will mesh
- [ ] Figure out how to best do piece movements:
        - Difference between ChessPieceCalculator and ChessMoveEngine
        - How the two will mesh
        - Should pieces be able to determine their own moves? Probably not
