package de.kopis.glacier.commands;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.OutputStream;
import java.util.Arrays;

import org.junit.Test;
import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import com.amazonaws.services.glacier.model.ListVaultsRequest;
import com.amazonaws.services.glacier.model.ListVaultsResult;
import de.kopis.glacier.printers.VaultPrinter;
import joptsimple.OptionSet;

public class ListVaultCommandTest extends AbstractCommandTest {
    @Test
    public void exec() throws Exception {
        final String vaultName = "dummyVaultName";

        final ListVaultsResult vaultList = new ListVaultsResult();
        final DescribeVaultOutput describeVaultOutput = new DescribeVaultOutput();
        describeVaultOutput.setVaultName(vaultName);
        vaultList.setVaultList(Arrays.asList(describeVaultOutput));
        expect(client.listVaults(isA(ListVaultsRequest.class))).andReturn(vaultList).times(1);
        replay(client);

        // there is no need to supply the LIST_VAULT option, because we're calling the command directly and it
        // does not take arguments anyway
        final OptionSet options = optionParser.parse();
        new ListVaultCommand(client, sqs, sns, new VaultPrinter() {
            @Override
            public void printVault(final DescribeVaultOutput output, final OutputStream o) {
                assertEquals(vaultName, output.getVaultName());
            }
        }).exec(options, optionParser);

        verify(client);
    }

}