package es.degrassi.appexp.api.storage;

import appeng.api.networking.IGridNode;
import appeng.api.storage.ILinkStatus;
import es.degrassi.appexp.api.entity.IExperienceInvHost;
import org.jetbrains.annotations.Nullable;

public interface IExperienceAccessTermMenuHost extends IExperienceInvHost {
  @Nullable
  IGridNode getGridNode();

  ILinkStatus getLinkStatus();
}
