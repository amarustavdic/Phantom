package com.ustavdica.state;

/**
 * Provides a read-only view of the game state.
 * <p>
 * This interface exposes non-mutative methods for querying the state, ensuring
 * that only StateHandler is responsible for modifications. It supports safe
 * access to state information for consumers like AI, UI, or debugging tools.
 */
public interface StateContext {
    // Read only methods to be specified bellow
}